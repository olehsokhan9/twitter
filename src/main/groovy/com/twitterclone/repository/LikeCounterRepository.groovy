package com.twitterclone.repository

import com.twitterclone.domain.LikeCounter
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeCounterRepository extends MongoRepository<LikeCounter, UUID> {
    LikeCounter getByPostId(UUID postId)
    Optional<LikeCounter> findByPostId(UUID postId)
    void deleteByPostId(UUID postId)
}