package com.twitterclone

import com.twitterclone.config.MongodbConfiguration
import com.twitterclone.dto.auth.Jwt
import com.twitterclone.repository.CommentCounterRepository
import com.twitterclone.repository.CommentRepository
import com.twitterclone.repository.FollowersRepository
import com.twitterclone.repository.LikeCounterRepository
import com.twitterclone.repository.LikeRepository
import com.twitterclone.repository.LockRepository
import com.twitterclone.repository.PostCreatedFanoutTaskRepository
import com.twitterclone.repository.PostRepository
import com.twitterclone.repository.PostShardByUserRepository
import com.twitterclone.repository.UserRepository
import com.twitterclone.repository.WallRepository
import com.twitterclone.service.AuthService
import com.twitterclone.utils.JwtUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification
import spock.lang.Stepwise

@AutoConfigureMockMvc
@Stepwise
@Import([MongodbConfiguration])
@SpringBootTest
@ActiveProfiles("test")
abstract class BaseIntegrationSpec extends Specification {

    @Autowired
    UserRepository userRepository
    @Autowired
    PostRepository postRepository
    @Autowired
    PostCreatedFanoutTaskRepository postCreatedFanoutTaskRepository
    @Autowired
    CommentCounterRepository commentCounterRepository
    @Autowired
    CommentRepository commentRepository
    @Autowired
    FollowersRepository followersRepository
    @Autowired
    LikeCounterRepository likeCounterRepository
    @Autowired
    LikeRepository likeRepository
    @Autowired
    LockRepository lockRepository
    @Autowired
    PostShardByUserRepository postShardByUserRepository
    @Autowired
    WallRepository wallRepository
    @Autowired
    AuthService authService
    @Autowired
    JwtUtil jwtUtil

    def cleanup() {
        userRepository.deleteAll()
        postRepository.deleteAll()
        postCreatedFanoutTaskRepository.deleteAll()
        commentCounterRepository.deleteAll()
        commentRepository.deleteAll()
        followersRepository.deleteAll()
        likeCounterRepository.deleteAll()
        likeRepository.deleteAll()
        lockRepository.deleteAll()
        postShardByUserRepository.deleteAll()
        wallRepository.deleteAll()
    }

    protected Jwt createTestUser(String username) {
        return authService.signup(username, "1q2w3e4r")
    }

    protected UUID extractUserId(String token) {
        return jwtUtil.extractUserId(token)
    }
}
