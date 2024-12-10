package com.twitterclone.service

import com.mongodb.client.MongoClient
import com.twitterclone.domain.post.Post
import com.twitterclone.domain.post.PostShardByUser
import com.twitterclone.domain.tasks.PostCreatedFanoutTask
import com.twitterclone.repository.CommentCounterRepository
import com.twitterclone.repository.LikeCounterRepository
import com.twitterclone.repository.PostCreatedFanoutTaskRepository
import com.twitterclone.repository.PostRepository
import com.twitterclone.repository.PostShardByUserRepository
import org.springframework.stereotype.Service

import static com.google.common.base.Preconditions.checkNotNull
import static com.twitterclone.domain.CommentCounter.Builder.commentCounter
import static com.twitterclone.domain.LikeCounter.Builder.likeCounter
import static com.twitterclone.domain.post.Post.Builder.post
import static com.twitterclone.domain.tasks.PostCreatedFanoutTaskState.CREATED
import static com.twitterclone.validations.PostValidation.validateContent
import static com.twitterclone.validations.PostValidation.validatePostCreatedBy
import static java.time.Instant.now
import static java.util.UUID.randomUUID

@Service
class PostService {

    PostRepository postRepository
    PostShardByUserRepository postShardByUserRepository
    LikeCounterRepository likeCounterRepository
    CommentCounterRepository commentCounterRepository
    PostCreatedFanoutTaskRepository postCreatedFanoutTaskRepository
    MongoClient mongoClient

    PostService(PostRepository postRepository,
                PostShardByUserRepository postShardByUserRepository,
                LikeCounterRepository likeCounterRepository,
                CommentCounterRepository commentCounterRepository,
                PostCreatedFanoutTaskRepository postCreatedFanoutTaskRepository,
                MongoClient mongoClient) {
        this.postRepository = postRepository
        this.postShardByUserRepository = postShardByUserRepository
        this.likeCounterRepository = likeCounterRepository
        this.commentCounterRepository = commentCounterRepository
        this.postCreatedFanoutTaskRepository = postCreatedFanoutTaskRepository
        this.mongoClient = mongoClient
    }

    Post create(UUID userId, String content) {
        checkNotNull(userId)
        validateContent(content)

        final def post = post()
                .id(randomUUID())
                .postedBy(userId)
                .content(content)
                .createdDate(Date.from(now()))
                .build()

        final def commentCounter = commentCounter()
                .postId(post.id())
                .count(0L)
                .build()

        final def likeCounter = likeCounter()
                .postId(post.id())
                .count(0L)
                .build()

        final def task = new PostCreatedFanoutTask(
            post.id(),
            userId,
            CREATED,
            Date.from(now()),
            null
        )

        final def postShardByUser = new PostShardByUser(
            randomUUID(),
            post.postedBy(),
            post.id(),
            post.createdDate()
        )

        try (final def session = mongoClient.startSession()) {
            session.startTransaction()

            try {
                postRepository.insert(post)
                postShardByUserRepository.insert(postShardByUser)
                commentCounterRepository.insert(commentCounter)
                likeCounterRepository.insert(likeCounter)
                postCreatedFanoutTaskRepository.insert(task)

                session.commitTransaction()
            } catch (Exception e) {
                session.abortTransaction()
                throw e
            }
        }

        return post
    }

    void update(UUID userId, UUID postId, String newContent) {
        validateContent(newContent)

        final def postOpt = postRepository.findById(postId)
        validatePostCreatedBy(postOpt, userId)

        final def updatedPost = postOpt.orElseThrow().updateContent(newContent)

        postRepository.save(updatedPost)
    }

    void delete(UUID userId, UUID postId) {
        final def post = postRepository.findById(postId)
        validatePostCreatedBy(post, userId)

        try (final def session = mongoClient.startSession()) {
            session.startTransaction()

            try {
                postRepository.delete(post.orElseThrow())
                commentCounterRepository.deleteByPostId(postId)
                likeCounterRepository.deleteByPostId(postId)
                postShardByUserRepository.deleteByPostedByAndPostId(userId, postId)

                session.commitTransaction()
            } catch (Exception e) {
                session.abortTransaction()
                throw e
            }
        }
    }
}
