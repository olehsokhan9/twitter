package com.twitterclone.repository

import com.twitterclone.domain.Like
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeRepository extends MongoRepository<Like, UUID> {
    Like getById(UUID id)
    Optional<Like> findByUserIdAndPostId(UUID userId, UUID postId)
}