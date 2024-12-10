package com.twitterclone.exceptions

import org.springframework.http.HttpStatus

import static com.twitterclone.errors.ErrorCodes.WRONG_PASSWORD

class WrongPasswordException extends BaseClientException {
    WrongPasswordException() {
        super("wrong password", WRONG_PASSWORD, HttpStatus.BAD_REQUEST)
    }
}