package com.twitterclone.repository

import com.twitterclone.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository extends MongoRepository<User, UUID> {
    Optional<User> findByUsername(String username)
    User getByUsername(String username)
    boolean existsByUsername(String username)
    User getById(UUID id)
}