package com.twitterclone.service

import com.mongodb.client.MongoClient
import com.twitterclone.exceptions.UserNotFoundException
import com.twitterclone.repository.FollowersRepository
import com.twitterclone.repository.UserRepository
import com.twitterclone.repository.WallRepository
import org.springframework.stereotype.Service

import static com.google.common.base.Preconditions.checkNotNull
import static com.google.common.base.Preconditions.checkNotNull
import static com.twitterclone.domain.Follower.Builder.follower
import static com.twitterclone.validations.PostValidation.validateContent
import static java.util.UUID.randomUUID

@Service
class FollowersService {

    private final FollowersRepository followersRepository
    private final WallRepository wallRepository
    private final WallService wallService
    private final UserRepository userRepository
    private final MongoClient mongoClient

    FollowersService(FollowersRepository followersRepository,
                     WallRepository wallRepository,
                     WallService wallService,
                     UserRepository userRepository,
                     MongoClient mongoClient) {
        this.followersRepository = followersRepository
        this.wallRepository = wallRepository
        this.userRepository = userRepository
        this.wallService = wallService
        this.mongoClient = mongoClient
    }

    void follow(UUID userId, UUID followeeId) {
        checkNotNull(followeeId)
        checkNotNull(userId)

        if (followersRepository.findByFolloweeIdAndFollowerId(followeeId, userId).isPresent())
            return

        if (userRepository.findById(followeeId).isEmpty())
            throw new UserNotFoundException()

        final def follower = follower()
            .id(randomUUID())
            .followerId(userId)
            .followeeId(followeeId)
            .build()

        try (final def session = mongoClient.startSession()) {
            session.startTransaction();

            try {
                followersRepository.insert(follower)
                // todo: should be a task, or executed based on event, with some check and delay after execution to prevent race conditions
                wallService.addTopPostsOnSubscribe(userId, followeeId)

                session.commitTransaction();
            } catch (Exception e) {
                session.abortTransaction()
                throw e
            }
        }
    }

    void unfollow(UUID userId, UUID followeeId) {
        final def follower = followersRepository.findByFolloweeIdAndFollowerId(followeeId, userId)
        if (follower.isEmpty()) return

        try (final def session = mongoClient.startSession()) {
            session.startTransaction()

            try {
                followersRepository.delete(follower.orElseThrow())
                // todo: should be a task, or executed based on event, with some check and delay after execution to prevent race conditions
                wallRepository.deleteAllByUserIdAndFolloweeId(userId, followeeId)

                session.commitTransaction()
            } catch (Exception e) {
                session.abortTransaction()
                throw e
            }
        }
    }

    // TODO: add ability to search with pages
    List<UUID> getFollowerIds(UUID followeeId) {
        return followersRepository.findAllByFolloweeId(followeeId).stream()
            .map(it -> it.followerId())
            .toList()
    }
}
