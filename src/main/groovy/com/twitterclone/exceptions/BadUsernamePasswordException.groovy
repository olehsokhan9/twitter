package com.twitterclone.exceptions

import org.springframework.http.HttpStatus

import static com.twitterclone.errors.ErrorCodes.WRONG_PASSWORD

class BadUsernamePasswordException extends BaseClientException {
    BadUsernamePasswordException() {
        super("not valid username or password", WRONG_PASSWORD, HttpStatus.BAD_REQUEST)
    }
}