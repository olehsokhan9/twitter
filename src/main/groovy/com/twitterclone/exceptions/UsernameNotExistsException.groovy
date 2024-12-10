package com.twitterclone.exceptions

import org.springframework.http.HttpStatus

import static com.twitterclone.errors.ErrorCodes.USERNAME_NOT_EXISTS

class UsernameNotExistsException extends BaseClientException {
    UsernameNotExistsException() {
        super("username not exists", USERNAME_NOT_EXISTS, HttpStatus.BAD_REQUEST)
    }
}