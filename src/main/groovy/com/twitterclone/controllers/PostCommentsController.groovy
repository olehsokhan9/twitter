package com.twitterclone.controllers

import com.twitterclone.dto.comment.CommentCreateRequest
import com.twitterclone.dto.comment.CommentCreateResponse
import com.twitterclone.dto.comment.CommentDto
import com.twitterclone.dto.comment.ListCommentsResponse
import com.twitterclone.service.CommentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.HttpStatus.CREATED

@RestController
@RequestMapping("/api/posts")
class PostCommentsController extends AbstractBaseController {

    private final CommentService commentService

    PostCommentsController(CommentService commentService) {
        this.commentService = commentService
    }

    @PostMapping("/{postId}/comment")
    ResponseEntity<CommentCreateResponse> addComment(@PathVariable("postId") UUID postId, @RequestBody CommentCreateRequest request) {
        final def userId = getUserId()

        final def comment = commentService.create(postId, userId, request.content())

        return new ResponseEntity<>(new CommentCreateResponse(comment.id()), CREATED)
    }

    @GetMapping("/{postId}/comments")
    ListCommentsResponse listComments(@PathVariable("postId") UUID postId, @RequestParam(defaultValue = "0") int page) {
        final def comments = commentService.find(postId, page)
        final def commentDtos = comments.stream()
            .map(it -> new CommentDto(it.userId(), it.content()))
            .toList()

        return new ListCommentsResponse(commentDtos)
    }
}
