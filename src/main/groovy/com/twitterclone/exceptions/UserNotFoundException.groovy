package com.twitterclone.exceptions

import org.springframework.http.HttpStatus

import static com.twitterclone.errors.ErrorCodes.USER_NOT_FOUND

class UserNotFoundException extends BaseClientException {
    UserNotFoundException() {
        super("user not found", USER_NOT_FOUND, HttpStatus.NOT_FOUND)
    }
}