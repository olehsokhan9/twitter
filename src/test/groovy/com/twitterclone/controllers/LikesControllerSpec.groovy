package com.twitterclone.controllers

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.repository.LikeCounterRepository
import com.twitterclone.repository.LikeRepository
import com.twitterclone.service.LikeService
import com.twitterclone.service.PostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class LikesControllerSpec extends BaseIntegrationSpec {

    @Autowired
    MockMvc mockMvc
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
        def mvcResult = mockMvc.perform(post("/api/posts/{postId}/like", createdPost.id())
                .header("Authorization", "Bearer ${jwt.token()}")
                .content(""))
                .andExpect(status().isCreated())
                .andReturn()

        then:
        mvcResult.response.contentAsString.isEmpty()
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
        def mvcResult = mockMvc.perform(delete("/api/posts/{postId}/like", createdPost.id())
                .header("Authorization", "Bearer ${jwt.token()}")
                .content(""))
                .andExpect(status().isNoContent())
                .andReturn()

        then:
        mvcResult.response.contentAsString.isEmpty()
        likeCounterRepository.getByPostId(createdPost.id()).count() == 0
        likeRepository.findByUserIdAndPostId(userId, createdPost.id()).isEmpty()
    }
}