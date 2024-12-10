package com.twitterclone.service

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.repository.UserRepository
import com.twitterclone.utils.JwtUtil
import org.springframework.beans.factory.annotation.Autowired

class UsersServiceSpec extends BaseIntegrationSpec {

    @Autowired
    AuthService authService
    @Autowired
    JwtUtil jwtUtil
    @Autowired
    UserRepository userRepository
    @Autowired
    UserService userService

    def "update user"() {
        given:
        def username = "user123"
        def password = "1q2w3e4r"
        def jwt = authService.signup(username, password)

        def newUsername = "user555"
        def userId = jwtUtil.extractUserId(jwt.token())

        when:
        userService.update(userId, newUsername)

        then:
        def user = userRepository.getByUsername(newUsername)
        user.username() == newUsername
        user.version() == 1L
    }

    def "delete user"() {
        given:
        def username = "user123"
        def password = "1q2w3e4r"
        def jwt = authService.signup(username, password)
        def userId = jwtUtil.extractUserId(jwt.token())

        when:
        userService.deleteUser(userId)

        then:
        def user = userRepository.findByUsername(username)
        user.isEmpty()
    }
}