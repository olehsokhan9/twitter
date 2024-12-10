package com.twitterclone.exceptions

import org.springframework.http.HttpStatus

import static com.twitterclone.errors.ErrorCodes.POST_NOT_FOUND

class PostNotFoundException extends BaseClientException {
    PostNotFoundException() {
        super("post not found", POST_NOT_FOUND, HttpStatus.NOT_FOUND)
    }
}