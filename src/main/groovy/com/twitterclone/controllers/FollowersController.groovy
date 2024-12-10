package com.twitterclone.controllers

import com.twitterclone.service.FollowersService

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NO_CONTENT

@RestController
@RequestMapping("/api/followee")
class FollowersController extends AbstractBaseController {

    private final FollowersService followersService

    FollowersController(FollowersService followersService) {
        this.followersService = followersService
    }

    @PostMapping("/{followeeId}")
    ResponseEntity<Void> follow(@PathVariable("followeeId") UUID followeeId) {
        final def userId = getUserId()

        followersService.follow(userId, followeeId)

        return new ResponseEntity<>(CREATED)
    }

    @DeleteMapping("/{followeeId}")
    ResponseEntity<Void> unfollow(@PathVariable("followeeId") UUID followeeId) {
        final def userId = getUserId()

        followersService.unfollow(userId, followeeId)

        return new ResponseEntity<>(NO_CONTENT)
    }
}
