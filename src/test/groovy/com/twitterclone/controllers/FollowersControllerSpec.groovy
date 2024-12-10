package com.twitterclone.controllers

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.repository.FollowersRepository
import com.twitterclone.service.FollowersService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class FollowersControllerSpec extends BaseIntegrationSpec {

    @Autowired
    MockMvc mockMvc
    @Autowired
    FollowersService followersService
    @Autowired
    FollowersRepository followersRepository

    def "follow user"() {
        given:
        def username1 = "user1"
        def username2 = "user2"
        def jwt1 = createTestUser(username1)
        def jwt2 = createTestUser(username2)

        def userId1 = extractUserId(jwt1.token())
        def userId2 = extractUserId(jwt2.token())

        when:
        def mvcResult = mockMvc.perform(post("/api/followee/{followeeId}", userId2)
                .header("Authorization", "Bearer ${jwt1.token()}")
                .content(""))
                .andExpect(status().isCreated())
                .andReturn()

        then:
        mvcResult.response.contentAsString.isEmpty()
        def follower = followersRepository.getByFolloweeIdAndFollowerId(userId2, userId1)
        follower.followeeId() == userId2
        follower.followerId() == userId1
    }

    def "unfollow user"() {
        given:
        def username1 = "user1"
        def username2 = "user2"
        def jwt1 = createTestUser(username1)
        def jwt2 = createTestUser(username2)

        def userId1 = extractUserId(jwt1.token())
        def userId2 = extractUserId(jwt2.token())

        followersService.follow(userId1, userId2)

        when:
        def mvcResult = mockMvc.perform(delete("/api/followee/{followeeId}", userId2)
                .header("Authorization", "Bearer ${jwt1.token()}")
                .content(""))
                .andExpect(status().isNoContent())
                .andReturn()

        then:
        mvcResult.response.contentAsString.isEmpty()
        followersRepository.findByFolloweeIdAndFollowerId(userId2, userId1).isEmpty()
    }
}