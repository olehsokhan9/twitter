package com.twitterclone.validations

import com.twitterclone.domain.User
import com.twitterclone.exceptions.BadUsernamePasswordException

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkNotNull
import static com.google.common.base.Preconditions.checkState

class UserValidation {

    private static final int USERNAME_MAX_LENGTH = 100
    private static final int PASSWORD_MAX_LENGTH = 100
    private static final int PASSWORD_MIN_LENGTH = 6
    private static final int ENCODED_PASSWORD_MAX_LENGTH = 1000

    static void validate(User user) {
        checkArgument(
                user.encodedPassword().size() < ENCODED_PASSWORD_MAX_LENGTH && user.encodedPassword().size() > 0,
                "encodedPassword length should be < %s and > 0, but %s".formatted(ENCODED_PASSWORD_MAX_LENGTH, user.encodedPassword().size())
        )
        checkArgument(
                user.username().size() < USERNAME_MAX_LENGTH && user.username().size() > 0,
                "username length should be < %s and > 0, but %s".formatted(USERNAME_MAX_LENGTH, user.username().size())
        )
    }

    static void validate(String username, String password) {
        validateUsername(username)
        validatePassword(password)
    }

    static void validateUsername(String username) {
        try {
            checkNotNull(username)
            checkState(username.length() > 0 && username.length() < USERNAME_MAX_LENGTH)
        } catch (Exception ignored) {
            throw new BadUsernamePasswordException()
        }
    }

    static void validatePassword(String password) {
        try {
            checkNotNull(password)
            checkState(password.length() >= PASSWORD_MIN_LENGTH && password.length() < PASSWORD_MAX_LENGTH)
        } catch (Exception ignored) {
            throw new BadUsernamePasswordException()
        }
    }
}
