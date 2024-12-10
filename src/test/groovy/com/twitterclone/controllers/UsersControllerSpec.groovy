package com.twitterclone.controllers

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.repository.UserRepository
import com.twitterclone.service.AuthService
import com.twitterclone.utils.JwtUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UsersControllerSpec extends BaseIntegrationSpec {

    @Autowired
    AuthService authService
    @Autowired
    JwtUtil jwtUtil
    @Autowired
    MockMvc mockMvc
    @Autowired
    UserRepository userRepository

    def "update user"() {
        given:
        def username = "user123"
        def password = "1q2w3e4r"
        def jwt = authService.signup(username, password)

        def newUsername = "user555"

        when:
        def mvcResult = mockMvc.perform(put("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${jwt.token()}")
                .content("""
                    {"username":"$newUsername"}
                """))
                .andExpect(status().isNoContent())
                .andReturn()

        then:
        def user = userRepository.getByUsername(newUsername)
        user.username() == newUsername
        user.version() == 1L
    }

    def "delete user"() {
        given:
        def username = "user123"
        def password = "1q2w3e4r"
        def jwt = authService.signup(username, password)

        when:
        def mvcResult = mockMvc.perform(delete("/api/users")
                .header("Authorization", "Bearer ${jwt.token()}")
                .content(""))
                .andExpect(status().isNoContent())
                .andReturn()

        then:
        def user = userRepository.findByUsername(username)
        user.isEmpty()
    }
}