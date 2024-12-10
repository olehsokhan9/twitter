package com.twitterclone.service

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.repository.LikeCounterRepository
import com.twitterclone.repository.LikeRepository
import org.springframework.beans.factory.annotation.Autowired

class LikesServiceSpec extends BaseIntegrationSpec {

    @Autowired
    PostService postService
    @Autowired
    LikeCounterRepository likeCounterRepository
    @Autowired
    LikeRepository likeRepository
    @Autowired
    LikeService likeService

    def "add like to post"() {
        given:
        def username = "user123"
        def jwt = createTestUser(username)
        def content = "my test post"

        def userId = extractUserId(jwt.token())

        def createdPost = postService.create(userId, content)

        when:
        likeService.like(userId, createdPost.id())

        then:
        likeCounterRepository.getByPostId(createdPost.id()).count() == 1
        likeRepository.findByUserIdAndPostId(userId, createdPost.id()).isPresent()
    }

    def "remove like from post"() {
        given:
        def username = "user123"
        def jwt = createTestUser(username)
        def content = "my test post"

        def userId = extractUserId(jwt.token())

        def createdPost = postService.create(userId, content)
        likeService.like(userId, createdPost.id())

        when:
        likeService.delete(userId, createdPost.id())

        then:
        likeCounterRepository.getByPostId(createdPost.id()).count() == 0
        likeRepository.findByUserIdAndPostId(userId, createdPost.id()).isEmpty()
    }
}