package com.twitterclone.repository

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.domain.User
import org.springframework.beans.factory.annotation.Autowired

import static java.util.UUID.randomUUID

class UserRepositorySpec extends BaseIntegrationSpec {

    @Autowired
    UserRepository userRepository

    def "should find a user by username"() {
        given:
        def user = new User(
            id: randomUUID(),
            username: "testUser",
            encodedPassword: "encodedPassword123",
            version: null
        )
        userRepository.save(user)

        when:
        Optional<User> result = userRepository.findByUsername("testUser")

        then: "The User is retrieved successfully"
        result.isPresent()
        result.get().username() == user.username()
    }

    def "should get a user by username"() {
        given:
        def user = new User(
            id: randomUUID(),
            username: "testUser",
            encodedPassword: "encodedPassword123",
            version: null
        )
        userRepository.save(user)

        when:
        def result = userRepository.getByUsername("testUser")

        then:
        result != null
        result.username() == "testUser"
    }

    def "should check if a username exists"() {
        given:
        def user = new User(
            id: randomUUID(),
            username: "existingUser",
            encodedPassword: "password123",
            version: null
        )
        userRepository.save(user)

        when:
        def result = userRepository.existsByUsername("existingUser")

        then:
        result
    }

    def "should return false if username does not exist"() {
        when:
        def result = userRepository.existsByUsername("nonExistentUser")

        then:
        !result
    }

    def "should retrieve a user by ID"() {
        given:
        def userId = randomUUID()
        def user = new User(
            id: userId,
            username: "userById",
            encodedPassword: "password123",
            version: null
        )
        userRepository.save(user)

        when:
        def result = userRepository.getById(userId)

        then:
        result != null
        result.id() == userId
    }
}