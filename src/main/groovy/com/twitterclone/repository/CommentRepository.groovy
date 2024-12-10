package com.twitterclone.repository

import com.twitterclone.domain.Comment
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository extends MongoRepository<Comment, UUID> {
    Comment getById(UUID id)
    List<Comment> findByUserIdAndPostId(UUID userId, UUID postId)

    @Query("{ 'postId': ?0 }")
    List<Comment> findByPostIdWithPagination(UUID postId, Pageable pageable);
}