package com.twitterclone.service

import com.mongodb.client.MongoClient
import com.twitterclone.domain.Comment
import com.twitterclone.domain.CommentCounter
import com.twitterclone.exceptions.PostNotFoundException
import com.twitterclone.repository.CommentRepository
import com.twitterclone.repository.PostRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service

import static com.google.common.base.Preconditions.checkNotNull
import static com.twitterclone.domain.Comment.Builder.comment
import static com.twitterclone.validations.PostValidation.validateContent
import static java.util.UUID.randomUUID

@Service
class CommentService {

    private static final int COMMENTS_PER_PAGE_SIZE = 50

    private final PostRepository postRepository
    private final CommentRepository commentRepository
    MongoClient mongoClient
    MongoTemplate mongoTemplate

    CommentService(PostRepository postRepository,
                   CommentRepository commentRepository,
                   MongoClient mongoClient,
                   MongoTemplate mongoTemplate) {
        this.postRepository = postRepository
        this.commentRepository = commentRepository
        this.mongoClient = mongoClient
        this.mongoTemplate = mongoTemplate
    }

    Comment create(UUID postId, UUID userId, String content) {
        checkNotNull(postId)
        checkNotNull(userId)
        validateContent(content)

        if (!postRepository.existsById(postId))
            throw new PostNotFoundException()

        final def comment = comment()
                .id(randomUUID())
                .postId(postId)
                .userId(userId)
                .content(content)
                .build()

        final def query = new Query(Criteria.where("_id").is(postId));
        final def update = new Update().inc("count", 1);

        try (final def session = mongoClient.startSession()) {
            session.startTransaction();

            try {
                commentRepository.insert(comment)
                mongoTemplate.updateFirst(query, update, CommentCounter.class);

                session.commitTransaction();
            } catch (Exception e) {
                session.abortTransaction()
                throw e
            }
        }

        return comment
    }

    List<Comment> find(UUID postId, int page) {
        if (!postRepository.existsById(postId))
            throw new PostNotFoundException()

        // todo: add filter by created date, so new comments won't break pagination search
        final def pageRequest = PageRequest.of(page, COMMENTS_PER_PAGE_SIZE)
        return commentRepository.findByPostIdWithPagination(postId, pageRequest)
    }
}
