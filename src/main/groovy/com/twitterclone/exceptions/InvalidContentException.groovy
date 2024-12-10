package com.twitterclone.exceptions

import org.springframework.http.HttpStatus

import static com.twitterclone.errors.ErrorCodes.WRONG_CONTENT

class InvalidContentException extends BaseClientException {
    InvalidContentException() {
        super("not valid content", WRONG_CONTENT, HttpStatus.BAD_REQUEST)
    }
}