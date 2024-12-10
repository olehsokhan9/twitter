package com.twitterclone.repository

import com.twitterclone.domain.wall.Wall
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface WallRepository extends MongoRepository<Wall, String> {
    Wall getById(String id)
    List<Wall> findByUserId(UUID userId, Pageable pageable)
    List<Wall> findByUserId(UUID userId)
    void deleteAllByUserIdAndFolloweeId(UUID userId, UUID followeeId)
}