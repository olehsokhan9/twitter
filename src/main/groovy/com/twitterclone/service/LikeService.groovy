package com.twitterclone.service

import com.mongodb.client.MongoClient
import com.twitterclone.domain.LikeCounter
import com.twitterclone.exceptions.PostNotFoundException
import com.twitterclone.repository.LikeRepository
import com.twitterclone.repository.PostRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service

import static com.google.common.base.Preconditions.checkNotNull
import static com.google.common.base.Preconditions.checkNotNull
import static com.twitterclone.domain.Like.Builder.like
import static java.util.UUID.randomUUID

@Service
class LikeService {

    LikeRepository likeRepository
    PostRepository postRepository
    MongoClient mongoClient
    MongoTemplate mongoTemplate

    LikeService(LikeRepository likeRepository,
                PostRepository postRepository,
                MongoClient mongoClient,
                MongoTemplate mongoTemplate) {
        this.likeRepository = likeRepository
        this.postRepository = postRepository
        this.mongoClient = mongoClient
        this.mongoTemplate = mongoTemplate
    }

    void like(UUID userId, UUID postId) {
        checkNotNull(postId)
        checkNotNull(userId)

        if (!postRepository.existsById(postId))
            throw new PostNotFoundException()

        if (likeRepository.findByUserIdAndPostId(userId, postId).isPresent())
            return

        final def like = like()
            .id(randomUUID())
            .userId(userId)
            .postId(postId)
            .build()

        final def query = new Query(Criteria.where("_id").is(postId))
        final def update = new Update().inc("count", 1)

        try (final def session = mongoClient.startSession()) {
            session.startTransaction()

            try {
                likeRepository.insert(like)
                mongoTemplate.updateFirst(query, update, LikeCounter.class)

                session.commitTransaction()
            } catch (Exception e) {
                session.abortTransaction()
                throw e
            }
        }
    }

    void delete(UUID userId, UUID postId) {
        final def like = likeRepository.findByUserIdAndPostId(userId, postId)
        if (like.isEmpty())
            return

        final def query = new Query(Criteria.where("_id").is(postId))
        final def update = new Update().inc("count", -1)

        try (final def session = mongoClient.startSession()) {
            session.startTransaction()

            try {
                likeRepository.delete(like.orElseThrow())
                mongoTemplate.updateFirst(query, update, LikeCounter.class)

                session.commitTransaction()
            } catch (Exception e) {
                session.abortTransaction()
                throw e
            }
        }
    }

}
