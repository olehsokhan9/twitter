package com.twitterclone.repository

import com.twitterclone.domain.post.Post
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository extends MongoRepository<Post, UUID> {
    Post getById(UUID id)
    List<Post> findAllByPostedBy(UUID postedBy)
    List<Post> findByPostedBy(UUID userId, Pageable pageable)
}