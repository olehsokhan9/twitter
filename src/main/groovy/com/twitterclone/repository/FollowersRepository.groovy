package com.twitterclone.repository

import com.twitterclone.domain.Follower
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface FollowersRepository extends MongoRepository<Follower, UUID> {
    Follower getByFolloweeIdAndFollowerId(UUID followeeId, UUID followerId)
    Optional<Follower> findByFolloweeIdAndFollowerId(UUID followeeId, UUID followerId)
    List<Follower> findAllByFolloweeId(UUID followeeId)
}