package com.twitterclone.controllers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.dto.wall.WallPost
import com.twitterclone.service.FollowersService
import com.twitterclone.service.PostService
import com.twitterclone.service.WallService
import org.json.JSONArray
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc

import static java.util.UUID.randomUUID
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class WallControllerSpec extends BaseIntegrationSpec {

    @Autowired
    MockMvc mockMvc
    @Autowired
    FollowersService followersService
    @Autowired
    WallService wallService
    @Autowired
    PostService postService

    def "get user wall"() {
        given:
        def username = "user1"
        def jwt1 = createTestUser(username)
        def userId1 = extractUserId(jwt1.token())

        for (def i = 0; i < 10; i++) {
            postService.create(userId1, "some content %s".formatted(i))
        }

        when:
        def mvcResult = mockMvc.perform(get("/api/wall/{userId}", userId1)
                .header("Authorization", "Bearer ${jwt1.token()}")
                .content(""))
                .andExpect(status().isOk())
                .andReturn()

        then:
        // todo validation against expected json file
        def json = new JSONArray(mvcResult.response.contentAsString)
        json.length() == 10
        json.getJSONObject(0).getString("content") == "some content 9"
        json.getJSONObject(9).getString("content") == "some content 0"
    }

    def "get empty wall for non existing user"() {
        given:
        def username = "user1"
        def jwt1 = createTestUser(username)
        def userId1 = extractUserId(jwt1.token())

        for (def i = 0; i < 10; i++) {
            postService.create(userId1, "some content %s".formatted(i))
        }

        when:
        def mvcResult = mockMvc.perform(get("/api/wall/{userId}", randomUUID())
                .header("Authorization", "Bearer ${jwt1.token()}")
                .content(""))
                .andExpect(status().isOk())
                .andReturn()

        then:
        def json = new JSONArray(mvcResult.response.contentAsString)
        json.length() == 0
    }

    def "get following wall"() {
        given:
        def username = "user"
        def jwt = createTestUser(username)
        def userId = extractUserId(jwt.token())

        for (def i = 0; i < 5; i++) {
            def followeeUsername = "user$i"
            def followeeJwt = createTestUser(followeeUsername)
            def followeeUserId = extractUserId(followeeJwt.token())

            for (def j = 0; j < 2; j++)
                postService.create(followeeUserId, "some content $i $j")

            followersService.follow(userId, followeeUserId)
        }

        when:
        def mvcResult = mockMvc.perform(get("/api/wall/following")
                .header("Authorization", "Bearer ${jwt.token()}")
                .content(""))
                .andExpect(status().isOk())
                .andReturn()

        then:
        def objectMapper = new ObjectMapper()
        def wallPosts = objectMapper.readValue(mvcResult.response.contentAsString, new TypeReference<List<WallPost>>() {})

        wallPosts.size() == 10

        // todo validation against expected json file
        wallPosts.get(0).content() == "some content 4 1"
        wallPosts.get(1).content() == "some content 4 0"
        wallPosts.get(8).content() == "some content 0 1"
        wallPosts.get(9).content() == "some content 0 0"
    }

}