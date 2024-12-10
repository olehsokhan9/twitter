package com.twitterclone.controllers

import com.twitterclone.dto.post.PostCreateRequest
import com.twitterclone.dto.post.PostCreateResponse
import com.twitterclone.dto.post.PostUpdateRequest
import com.twitterclone.service.PostService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NO_CONTENT

@RestController
@RequestMapping("/api/posts")
class PostsController extends AbstractBaseController {

    PostService postService

    PostsController(PostService postService) {
        this.postService = postService
    }

    @PostMapping
    ResponseEntity<PostCreateResponse> create(@RequestBody PostCreateRequest createRequest) {
        final def userId = getUserId()
        final def post = postService.create(userId, createRequest.content())
        return new ResponseEntity<>(new PostCreateResponse(post.id()), CREATED)
    }

    @PutMapping("/{postId}")
    ResponseEntity<Void> update(@RequestBody PostUpdateRequest postUpdateRequest, @PathVariable("postId") UUID postId) {
        final def userId = getUserId()
        postService.update(userId, postId, postUpdateRequest.content())

        return new ResponseEntity<>(NO_CONTENT)
    }

    @DeleteMapping("/{postId}")
    ResponseEntity<Void> delete(@PathVariable("postId") UUID postId) {
        final def userId = getUserId()
        postService.delete(userId, postId)

        return new ResponseEntity<>(NO_CONTENT)
    }
}
