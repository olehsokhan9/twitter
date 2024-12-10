package com.twitterclone.service

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.repository.CommentCounterRepository
import com.twitterclone.repository.LikeCounterRepository
import com.twitterclone.repository.PostRepository
import com.twitterclone.repository.PostShardByUserRepository
import com.twitterclone.repository.WallRepository
import org.springframework.beans.factory.annotation.Autowired

class PostsServiceSpec extends BaseIntegrationSpec {

    @Autowired
    AuthService authService
    @Autowired
    PostRepository postRepository
    @Autowired
    PostService postService
    @Autowired
    FollowersService followersService
    @Autowired
    CommentCounterRepository commentCounterRepository
    @Autowired
    LikeCounterRepository likeCounterRepository
    @Autowired
    PostShardByUserRepository postShardByUserRepository
    @Autowired
    WallRepository wallRepository

    def "create post"() {
        given:
        def username = "user123"
        def jwt = createTestUser(username)
        def content = "my test post"

        def userId = extractUserId(jwt.token())

        when:
        def post = postService.create(userId, content)

        then:
        def storedPost = postRepository.getById(post.id())
        storedPost.content() == content
        storedPost.postedBy() == userId
        storedPost == post

        commentCounterRepository.getByPostId(post.id()).count() == 0
        likeCounterRepository.getByPostId(post.id()).count() == 0
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
        postService.update(userId, post.id(), newContent)

        then:
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
        postService.delete(userId, post.id())

        then:
        postRepository.findById(post.id()).isEmpty()

        commentCounterRepository.findByPostId(post.id()).isEmpty()
        likeCounterRepository.findByPostId(post.id()).isEmpty()
        postShardByUserRepository.findByPostedByAndPostId(post.postedBy(), post.id()).isEmpty()
    }

    def "create post send to followers walls"() {
        given:
        def followee = "followee"
        def followeeJwt = createTestUser(followee)

        def userId1 = extractUserId(createTestUser("user1").token())
        def userId2 = extractUserId(createTestUser("user2").token())
        def userId3 = extractUserId(createTestUser("user3").token())

        def followeeUserId = extractUserId(followeeJwt.token())

        followersService.follow(userId1, followeeUserId)
        followersService.follow(userId2, followeeUserId)
        followersService.follow(userId3, followeeUserId)

        when:
        def post = postService.create(followeeUserId,  "my test post")
        // todo: add sleep by condition (check db periodically), throw after timeout
        Thread.sleep(3_000) // wait 3s, post is publishing to followers walls

        then:
        def wallUser1 = wallRepository.findByUserId(userId1)
        wallUser1.size() == 1
        wallUser1.get(0).postId() == post.id()
        wallUser1.get(0).followeeId() == followeeUserId

        def wallUser2 = wallRepository.findByUserId(userId2)
        wallUser2.size() == 1
        wallUser2.get(0).postId() == post.id()
        wallUser2.get(0).followeeId() == followeeUserId

        def wallUser3 = wallRepository.findByUserId(userId3)
        wallUser3.size() == 1
        wallUser3.get(0).postId() == post.id()
        wallUser3.get(0).followeeId() == followeeUserId
    }
}