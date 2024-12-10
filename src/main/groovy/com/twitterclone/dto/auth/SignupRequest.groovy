package com.twitterclone.dto.auth

record SignupRequest(
    String username,
    String password
) {
    @Override
    String toString() {
        return "username: ***, password: ***"
    }
}