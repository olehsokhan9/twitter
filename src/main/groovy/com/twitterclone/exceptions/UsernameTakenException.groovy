package com.twitterclone.exceptions

import org.springframework.http.HttpStatus

import static com.twitterclone.errors.ErrorCodes.USERNAME_TAKEN

class UsernameTakenException extends BaseClientException {
    UsernameTakenException(String message) {
        super(message, USERNAME_TAKEN, HttpStatus.CONFLICT)
    }
}