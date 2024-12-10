package com.twitterclone.repository

import com.twitterclone.domain.CommentCounter
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentCounterRepository extends MongoRepository<CommentCounter, UUID> {
    CommentCounter getByPostId(UUID postId)
    Optional<CommentCounter> findByPostId(UUID postId)
    void deleteByPostId(UUID postId)
}