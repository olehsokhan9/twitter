package com.twitterclone.controllers

import com.twitterclone.dto.user.UserUpdateRequest
import com.twitterclone.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.HttpStatus.NO_CONTENT

@RestController
@RequestMapping("/api/users")
class UsersController extends AbstractBaseController {

    private UserService userService

    UsersController(UserService userService) {
        this.userService = userService
    }

    @PutMapping
    ResponseEntity<Void> update(@RequestBody UserUpdateRequest request) {
        final def userId = getUserId()
        userService.update(userId, request.username())

        return new ResponseEntity<>(NO_CONTENT)
    }

    @DeleteMapping
    ResponseEntity<Void> delete() {
        final def userId = getUserId()
        userService.deleteUser(userId)

        return new ResponseEntity<>(NO_CONTENT)
    }
}
