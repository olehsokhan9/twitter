package com.twitterclone.controllers

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.repository.UserRepository
import com.twitterclone.service.AuthService
import com.twitterclone.utils.JwtUtil
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class AuthControllerSpec extends BaseIntegrationSpec {

    @Autowired
    AuthService authService
    @Autowired
    JwtUtil jwtUtil
    @Autowired
    MockMvc mockMvc
    @Autowired
    UserRepository userRepository

    def "signup user"() {
        given:
        def username = "user123"
        def password = "1q2w3e4r"

        when:
        def mvcResult = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"$username", "password":"$password"}
                """))
                .andExpect(status().isOk())
                .andReturn()

        then:
        def responseContent = mvcResult.response.contentAsString
        def responseAsJson = new JSONObject(responseContent)

        responseAsJson.has("token")

        def user = userRepository.findByUsername(username)
        user.isPresent()
        user.orElseThrow().username() == username
        user.orElseThrow().encodedPassword() != password
        user.orElseThrow().id() == jwtUtil.extractUserId(responseAsJson.getString("token"))
    }

    def "login user"() {
        given:
        def username = "user123"
        def password = "1q2w3e4r"
        authService.signup(username, password)

        when:
        def mvcResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"$username", "password":"$password"}
                """))
                .andExpect(status().isOk())
                .andReturn()

        then:
        def responseContent = mvcResult.response.contentAsString
        def responseAsJson = new JSONObject(responseContent)

        responseAsJson.has("token")

        def user = userRepository.findByUsername(username)
        user.isPresent()
        user.orElseThrow().username() == username
        user.orElseThrow().encodedPassword() != password
        user.orElseThrow().id() == jwtUtil.extractUserId(responseAsJson.getString("token"))
    }

    def "return validation error when username missed for signup"() {
        given:
        def password = "1q2w3e4r"

        when:
        def mvcResult = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"password":"$password"}
                """))
                .andExpect(status().isBadRequest())
                .andReturn()

        then:
        def responseContent = mvcResult.response.contentAsString
        def responseAsJson = new JSONObject(responseContent)

        responseAsJson.has("code")
        responseAsJson.has("error")

        responseAsJson.getInt("code") == 400003
        responseAsJson.getString("error") == "not valid username or password"
    }

    def "return validation error when password missed for signup"() {
        given:
        def username = "user123"

        when:
        def mvcResult = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"$username"}
                """))
                .andExpect(status().isBadRequest())
                .andReturn()

        then:
        def responseContent = mvcResult.response.contentAsString
        def responseAsJson = new JSONObject(responseContent)

        responseAsJson.has("code")
        responseAsJson.has("error")

        responseAsJson.getInt("code") == 400003
        responseAsJson.getString("error") == "not valid username or password"
    }

    def "return validation error when username missed for login"() {
        given:
        def password = "1q2w3e4r"

        when:
        def mvcResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"password":"$password"}
                """))
                .andExpect(status().isBadRequest())
                .andReturn()

        then:
        def responseContent = mvcResult.response.contentAsString
        def responseAsJson = new JSONObject(responseContent)

        responseAsJson.has("code")
        responseAsJson.has("error")

        responseAsJson.getInt("code") == 400003
        responseAsJson.getString("error") == "not valid username or password"
    }

    def "return validation error when password missed for login"() {
        given:
        def username = "user123"

        when:
        def mvcResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"$username"}
                """))
                .andExpect(status().isBadRequest())
                .andReturn()

        then:
        def responseContent = mvcResult.response.contentAsString
        def responseAsJson = new JSONObject(responseContent)

        responseAsJson.has("code")
        responseAsJson.has("error")

        responseAsJson.getInt("code") == 400003
        responseAsJson.getString("error") == "not valid username or password"
    }
}