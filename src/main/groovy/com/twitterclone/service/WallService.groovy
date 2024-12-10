package com.twitterclone.service

import com.twitterclone.domain.CommentCounter
import com.twitterclone.domain.LikeCounter
import com.twitterclone.domain.post.Post
import com.twitterclone.domain.wall.Wall
import com.twitterclone.domain.wall.WallCompositeKeyProvider
import com.twitterclone.dto.wall.WallPost
import com.twitterclone.repository.CommentCounterRepository
import com.twitterclone.repository.LikeCounterRepository
import com.twitterclone.repository.PostRepository
import com.twitterclone.repository.PostShardByUserRepository
import com.twitterclone.repository.WallRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

import java.util.stream.Collectors

import static com.google.common.base.Preconditions.checkNotNull
import static com.google.common.base.Preconditions.checkState
import static java.util.Collections.emptyList
import static org.springframework.data.domain.Sort.Direction.DESC

@Service
class WallService {

    private static final int WALLET_FETCH_PAGE_SIZE = 20

    LikeCounterRepository likeCounterRepository
    CommentCounterRepository commentCounterRepository
    PostRepository postRepository
    PostShardByUserRepository postShardByUserRepository
    WallRepository wallRepository

    WallService(LikeCounterRepository likeCounterRepository,
                CommentCounterRepository commentCounterRepository,
                PostRepository postRepository,
                PostShardByUserRepository postShardByUserRepository,
                WallRepository wallRepository) {
        this.likeCounterRepository = likeCounterRepository
        this.commentCounterRepository = commentCounterRepository
        this.postRepository = postRepository
        this.postShardByUserRepository = postShardByUserRepository
        this.wallRepository = wallRepository
    }

    List<WallPost> fetchWall(UUID userId, int page) {
        checkNotNull(userId)
        checkState(page >= 0)

        final def pageRequest = PageRequest.of(page, WALLET_FETCH_PAGE_SIZE, Sort.by(DESC, "createdDate"))

        final def userWall = wallRepository.findByUserId(userId, pageRequest)
        if (userWall.isEmpty()) return emptyList()

        final def postIds = userWall.stream().map(it -> it.postId()).toList()

        // todo: can be 3 parallel calls
        final Map<UUID, Post> postById = postRepository.findAllById(postIds).stream()
            .collect(Collectors.toMap(Post::id, it -> it))

        final Map<UUID, LikeCounter> likeCountByPostId = likeCounterRepository.findAllById(postIds).stream()
                .collect(Collectors.toMap(LikeCounter::postId, it -> it))

        final Map<UUID, CommentCounter> commentCountByPostId = commentCounterRepository.findAllById(postIds).stream()
                .collect(Collectors.toMap(CommentCounter::postId, it -> it))

        return userWall.stream()
            .filter(it -> postById.containsKey(it.postId()))
            .map(it -> {
                return new WallPost(
                    it.postId(),
                    postById.get(it.postId()).content(),
                    likeCountByPostId.get(it.postId()).count(),
                    commentCountByPostId.get(it.postId()).count()
                )
            }).toList()
    }

    List<WallPost> fetchUserPosts(UUID userId, int page) {
        checkNotNull(userId)
        checkState(page >= 0)

        final def pageRequest = PageRequest.of(page, WALLET_FETCH_PAGE_SIZE, Sort.by(DESC, "createdDate"))

        final def userPosts = postShardByUserRepository.findByPostedBy(userId, pageRequest)
        final def postIds = userPosts.stream().map(it -> it.postId()).toList()

        // todo: can be 3 parallel calls
        final Map<UUID, Post> postById = postRepository.findAllById(postIds).stream()
            .collect(Collectors.toMap(Post::id, it -> it))

        final Map<UUID, LikeCounter> likeCountByPostId = likeCounterRepository.findAllById(postIds).stream()
                .collect(Collectors.toMap(LikeCounter::postId, it -> it))

        final Map<UUID, CommentCounter> commentCountByPostId = commentCounterRepository.findAllById(postIds).stream()
                .collect(Collectors.toMap(CommentCounter::postId, it -> it))

        return postIds.stream()
            .filter(postId -> postById.containsKey(postId))
            .map(postId -> {
                return new WallPost(
                    postId,
                    postById.get(postId).content(),
                    likeCountByPostId.get(postId).count(),
                    commentCountByPostId.get(postId).count()
                )
            }).toList()
    }

    void addTopPostsOnSubscribe(UUID userId, UUID followeeId) {
        checkNotNull(userId)
        checkNotNull(followeeId)

        final def userPosts = postShardByUserRepository.findByPostedBy(
            followeeId,
            PageRequest.of(0, WALLET_FETCH_PAGE_SIZE, Sort.by(DESC, "createdDate"))
        )

        final def postIds = userPosts.stream().map(it -> it.postId()).toList()
        final def posts = postRepository.findAllById(postIds)

        final def userWallPosts = posts.stream().map(it -> {
            final def wallId = WallCompositeKeyProvider.compositeId(userId, it.id())
            return new Wall(
                wallId,
                userId,
                it.id(),
                followeeId,
                it.createdDate()
            )
        }).toList()

        wallRepository.saveAll(userWallPosts)
    }

}
