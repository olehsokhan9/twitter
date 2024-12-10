package com.twitterclone.service

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.repository.CommentCounterRepository
import com.twitterclone.repository.CommentRepository
import org.springframework.beans.factory.annotation.Autowired

class CommentServiceSpec extends BaseIntegrationSpec {

    @Autowired
    PostService postService
    @Autowired
    CommentCounterRepository commentCounterRepository
    @Autowired
    CommentRepository commentRepository
    @Autowired
    CommentService commentService

    def "add comment to post"() {
        given:
        def username = "user123"
        def jwt = createTestUser(username)
        def content = "my test post"

        def userId = extractUserId(jwt.token())

        def createdPost = postService.create(userId, content)
        def commentContent = "test comment"

        when:
        def comment = commentService.create(createdPost.id(), userId, commentContent)

        then:
        comment.postId() == createdPost.id()
        comment.userId() == userId
        comment.content() == commentContent

        commentRepository.findById(comment.id()).isPresent()
    }

    def "find post comments"() {
        given:
        def username = "user123"
        def jwt = createTestUser(username)
        def content = "my test post"

        def userId = extractUserId(jwt.token())

        def createdPost = postService.create(userId, content)

        for (def i = 0; i < 10; i++) {
            commentService.create(createdPost.id(), userId, "test comment $i")
        }

        when:
        def comments = commentService.find(createdPost.id(), 0)

        then:
        comments.size() == 10
        comments.stream().allMatch(it -> it.userId() == userId)
        comments.stream().allMatch(it -> it.postId() == createdPost.id())
    }
}