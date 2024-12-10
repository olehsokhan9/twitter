package com.twitterclone.service

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.exceptions.BadUsernamePasswordException
import com.twitterclone.repository.UserRepository
import com.twitterclone.utils.JwtUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class AuthServiceSpec extends BaseIntegrationSpec {

    @Autowired
    AuthService authService
    @Autowired
    JwtUtil jwtUtil
    @Autowired
    UserRepository userRepository

    def "signup user"() {
        given:
        def username = "user123"
        def password = "1q2w3e4r"

        when:
        def jwt = authService.signup(username, password)

        then:
        jwt != null

        def user = userRepository.findByUsername(username)
        user.isPresent()
        user.orElseThrow().username() == username
        user.orElseThrow().encodedPassword() != password
        user.orElseThrow().id() == jwtUtil.extractUserId(jwt.token())
    }

    def "login user"() {
        given:
        def username = "user123"
        def password = "1q2w3e4r"
        authService.signup(username, password)

        when:
        def jwt = authService.login(username, password)

        then:
        jwt != null

        def user = userRepository.findByUsername(username)
        user.isPresent()
        user.orElseThrow().username() == username
        user.orElseThrow().encodedPassword() != password
        user.orElseThrow().id() == jwtUtil.extractUserId(jwt.token())
    }

    def "return validation error when username missed for signup"() {
        given:
        def password = "1q2w3e4r"

        when:
        authService.signup(null, password)

        then:
        def exception = thrown(BadUsernamePasswordException)
        exception.code == 400003
        exception.message == "not valid username or password"
        exception.httpStatus == HttpStatus.BAD_REQUEST
    }

    def "return validation error when password missed for signup"() {
        given:
        def username = "user12"

        when:
        authService.signup(username, null)

        then:
        def exception = thrown(BadUsernamePasswordException)
        exception.code == 400003
        exception.message == "not valid username or password"
        exception.httpStatus == HttpStatus.BAD_REQUEST
    }

    def "return validation error when username missed for login"() {
        given:
        def password = "1q2w3e4r"

        when:
        authService.login(null, password)

        then:
        def exception = thrown(BadUsernamePasswordException)
        exception.code == 400003
        exception.message == "not valid username or password"
        exception.httpStatus == HttpStatus.BAD_REQUEST
    }

    def "return validation error when password missed for login"() {
        given:
        def username = "user12"

        when:
        authService.login(username, null)

        then:
        def exception = thrown(BadUsernamePasswordException)
        exception.code == 400003
        exception.message == "not valid username or password"
        exception.httpStatus == HttpStatus.BAD_REQUEST
    }
}