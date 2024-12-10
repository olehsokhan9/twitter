package com.twitterclone.controllers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.dto.comment.ListCommentsResponse
import com.twitterclone.repository.CommentCounterRepository
import com.twitterclone.repository.CommentRepository
import com.twitterclone.service.CommentService
import com.twitterclone.service.PostService
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CommentsControllerSpec extends BaseIntegrationSpec {

    @Autowired
    MockMvc mockMvc
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
        def commentContext = "test comment"

        when:
        def mvcResult = mockMvc.perform(post("/api/posts/{postId}/comment", createdPost.id())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${jwt.token()}")
                .content("""
                    {"content":"${commentContext}"}
                """))
                .andExpect(status().isCreated())
                .andReturn()

        then:
        def responseContent = mvcResult.response.contentAsString
        def responseAsJson = new JSONObject(responseContent)

        responseAsJson.has("id")
        def commentId = UUID.fromString(responseAsJson.getString("id"))

        // todo validation against expected json file
        commentCounterRepository.getByPostId(createdPost.id()).count() == 1
        def userComments = commentRepository.findByUserIdAndPostId(userId, createdPost.id())
        userComments.size() == 1
        userComments.get(0).content() == commentContext
        userComments.get(0).id() == commentId
    }

    def "get post comments"() {
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
        def mvcResult = mockMvc.perform(get("/api/posts/{postId}/comments", createdPost.id())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${jwt.token()}")
                .content(""))
                .andExpect(status().isOk())
                .andReturn()

        then:
        def objectMapper = new ObjectMapper()
        def result = objectMapper.readValue(mvcResult.response.contentAsString, new TypeReference<ListCommentsResponse>() {})

        // todo validation against expected json file
        result.comments().size() == 10
        result.comments().stream().allMatch(it -> it.userId() == userId)
    }
}