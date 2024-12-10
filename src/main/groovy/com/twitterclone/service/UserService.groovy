package com.twitterclone.service

import com.twitterclone.repository.UserRepository
import org.springframework.stereotype.Service

import static com.google.common.base.Preconditions.checkNotNull
import static com.twitterclone.validations.UserValidation.validateUsername

@Service
class UserService {

    UserRepository userRepository

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    void update(UUID userId, String newUsername) {
        checkNotNull(userId)
        validateUsername(newUsername)

        final def user = userRepository.getById(userId)
        final def updatedUser = user.updateUsername(newUsername)

        if (user != updatedUser) {
            userRepository.save(updatedUser)
        }
    }

    void deleteUser(UUID userId) {
        // todo: should be a job, where we delete all the records from different tables related to this userId
        userRepository.findById(userId).ifPresent(user -> {
            userRepository.delete(user)
        })
    }

    boolean userExists(UUID userId) {
        // todo: cache can be added here
        return userRepository.findById(userId).isPresent()
    }
}
