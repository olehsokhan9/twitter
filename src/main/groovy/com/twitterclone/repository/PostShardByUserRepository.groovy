package com.twitterclone.repository

import com.twitterclone.domain.post.PostShardByUser
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PostShardByUserRepository extends MongoRepository<PostShardByUser, UUID> {
    PostShardByUser getByPostedByAndPostId(UUID userId, UUID postId)
    Optional<PostShardByUser> findByPostedByAndPostId(UUID userId, UUID postId)
    List<PostShardByUser> findByPostedBy(UUID userId, Pageable pageable)
    void deleteByPostedByAndPostId(UUID userId, UUID postId)
}