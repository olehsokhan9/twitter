package com.twitterclone.controllers

import com.twitterclone.dto.auth.LoginRequest
import com.twitterclone.dto.auth.LoginResponse
import com.twitterclone.dto.auth.SignupRequest

import com.twitterclone.dto.auth.SignupResponse
import com.twitterclone.service.AuthService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController {

    private final AuthService authService

    AuthController(AuthService authService) {
        this.authService = authService
    }

    @PostMapping("/login")
    LoginResponse login(@RequestBody LoginRequest loginRequest) {
        final def jwt = authService.login(loginRequest.username(), loginRequest.password())
        return new LoginResponse(jwt.token())
    }

    @PostMapping("/signup")
    SignupResponse signup(@RequestBody SignupRequest signupRequest) {
        final def jwt = authService.signup(signupRequest.username(), signupRequest.password())
        return new SignupResponse(jwt.token())
    }
}