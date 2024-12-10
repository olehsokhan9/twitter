package com.twitterclone.controllers

import com.twitterclone.service.LikeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NO_CONTENT

@RestController
@RequestMapping("/api/posts")
class LikesController extends AbstractBaseController {

    private LikeService likeService

    LikesController(LikeService likeService) {
        this.likeService = likeService
    }

    @PostMapping("/{postId}/like")
    ResponseEntity<Void> addLike(@PathVariable("postId") UUID postId) {
        final def userId = getUserId()
        likeService.like(userId, postId)

        return new ResponseEntity<>(CREATED)
    }

    @DeleteMapping("/{postId}/like")
    ResponseEntity<Void> removeLike(@PathVariable("postId") UUID postId) {
        final def userId = getUserId()
        likeService.delete(userId, postId)

        return new ResponseEntity<>(NO_CONTENT)
    }
}
