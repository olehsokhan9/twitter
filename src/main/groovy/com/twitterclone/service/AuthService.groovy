package com.twitterclone.service

import com.twitterclone.dto.auth.Jwt
import com.twitterclone.exceptions.UsernameNotExistsException
import com.twitterclone.exceptions.UsernameTakenException
import com.twitterclone.exceptions.WrongPasswordException
import com.twitterclone.repository.UserRepository
import com.twitterclone.utils.JwtUtil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

import static com.twitterclone.domain.User.Builder.user
import static com.twitterclone.validations.UserValidation.validate
import static java.util.UUID.randomUUID

@Service
class AuthService {

    UserRepository userRepository
    PasswordEncoder passwordEncoder
    JwtUtil jwtUtil

    AuthService(UserRepository userRepository,
                PasswordEncoder passwordEncoder,
                JwtUtil jwtUtil) {
        this.userRepository = userRepository
        this.passwordEncoder = passwordEncoder
        this.jwtUtil = jwtUtil
    }

    Jwt signup(String username, String password) {
        validate(username, password)

        final def userExists = userRepository.existsByUsername(username)
        if (userExists) {
            throw new UsernameTakenException("username already taken")
        }

        final def encodedPassword = passwordEncoder.encode(password)
        final def user = user()
            .id(randomUUID())
            .username(username)
            .encodedPassword(encodedPassword)
            .build()

        userRepository.insert(user)

        return new Jwt(jwtUtil.generateToken(user.id()))
    }

    Jwt login(String username, String password) {
        validate(username, password)

        final def userOpt = userRepository.findByUsername(username)
        if (userOpt.isEmpty()) {
            throw new UsernameNotExistsException()
        }

        final def user = userOpt.orElseThrow()
        if (!passwordEncoder.matches(password, user.encodedPassword())) {
            throw new WrongPasswordException()
        }

        return new Jwt(jwtUtil.generateToken(user.id()))
    }
}
