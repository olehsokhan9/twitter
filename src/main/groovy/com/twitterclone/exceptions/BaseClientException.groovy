package com.twitterclone.exceptions

import org.springframework.http.HttpStatus

class BaseClientException extends RuntimeException {
    int code
    HttpStatus httpStatus
    BaseClientException(String message, int code, HttpStatus httpStatus) {
        super(message)
        this.code = code
        this.httpStatus = httpStatus
    }
}