package com.twitterclone.repository

import com.twitterclone.domain.Lock
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LockRepository extends MongoRepository<Lock, String> {
    Lock getById(String id)
}