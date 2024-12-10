package com.twitterclone.exceptions

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BaseClientException.class)
    @ResponseBody
    ResponseEntity<Map<String, Object>> handleCustomAuthException(BaseClientException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", e.getMessage());
        body.put("code", e.getCode());

        return new ResponseEntity<>(body, e.httpStatus)
    }
}