package com.twitterclone.dto.auth

record LoginRequest(
    String username,
    String password
) {
    @Override
    String toString() {
        return "username: %s, password: ***".formatted(username)
    }
}