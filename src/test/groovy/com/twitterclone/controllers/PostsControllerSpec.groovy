package com.twitterclone.controllers

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.repository.CommentCounterRepository
import com.twitterclone.repository.LikeCounterRepository
import com.twitterclone.repository.PostRepository
import com.twitterclone.repository.PostShardByUserRepository
import com.twitterclone.service.AuthService
import com.twitterclone.service.PostService
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PostsControllerSpec extends BaseIntegrationSpec {

    @Autowired
    AuthService authService
    @Autowired
    MockMvc mockMvc
    @Autowired
    PostRepository postRepository
    @Autowired
    PostService postService
    @Autowired
    CommentCounterRepository commentCounterRepository
    @Autowired
    LikeCounterRepository likeCounterRepository
    @Autowired
    PostShardByUserRepository postShardByUserRepository

    def "create post"() {
        given:
        def username = "user123"
        def jwt = createTestUser(username)
        def content = "my test post"

        def userId = extractUserId(jwt.token())

        when:
        def mvcResult = mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${jwt.token()}")
                .content("""
                    {"content":"$content"}
                """))
                .andExpect(status().isCreated())
                .andReturn()

        then:
        def responseContent = mvcResult.response.contentAsString
        def responseAsJson = new JSONObject(responseContent)

        responseAsJson.has("id")
        def postId = UUID.fromString(responseAsJson.getString("id"))

        def post = postRepository.getById(postId)
        post.content() == content
        userId == post.postedBy()

        commentCounterRepository.getByPostId(postId).count() == 0
        likeCounterRepository.getByPostId(postId).count() == 0
        postShardByUserRepository.getByPostedByAndPostId(post.postedBy(), post.id()).createdDate() == post.createdDate()
    }

    def "update post"() {
        given:
        def jwt = createTestUser("user123")
        def userId = extractUserId(jwt.token())

        def content = "my test post"
        def newContent = "my new test post"

        def post = postService.create(userId, content)

        when:
        def mvcResult = mockMvc.perform(put("/api/posts/{postId}", post.id())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${jwt.token()}")
                .content("""
                    {"content":"$newContent"}
                """))
                .andExpect(status().isNoContent())
                .andReturn()

        then:
        mvcResult.response.contentAsString.isEmpty()

        def updatedPost = postRepository.getById(post.id())
        updatedPost.content() == newContent
        userId == post.postedBy()

        commentCounterRepository.getByPostId(post.id()).count() == 0
        likeCounterRepository.getByPostId(post.id()).count() == 0
        postShardByUserRepository.getByPostedByAndPostId(post.postedBy(), post.id()).createdDate() == post.createdDate()
    }

    def "delete post"() {
        given:
        def jwt = createTestUser("user123")
        def userId = extractUserId(jwt.token())

        def content = "my test post"
        def post = postService.create(userId, content)

        when:
        def mvcResult = mockMvc.perform(delete("/api/posts/{postId}", post.id())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${jwt.token()}")
                .content(""))
                .andExpect(status().isNoContent())
                .andReturn()

        then:
        mvcResult.response.contentAsString.isEmpty()
        postRepository.findById(post.id()).isEmpty()

        commentCounterRepository.findByPostId(post.id()).isEmpty()
        likeCounterRepository.findByPostId(post.id()).isEmpty()
        postShardByUserRepository.findByPostedByAndPostId(post.postedBy(), post.id()).isEmpty()
    }
}