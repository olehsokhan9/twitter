package com.twitterclone.dto.user

record UserUpdateRequest(
    String username
) {
    @Override
    String toString() {
        return "username: ***"
    }
}