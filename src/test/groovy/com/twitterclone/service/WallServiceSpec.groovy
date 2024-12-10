package com.twitterclone.service

import com.twitterclone.BaseIntegrationSpec
import org.springframework.beans.factory.annotation.Autowired

import static java.util.UUID.randomUUID

class WallServiceSpec extends BaseIntegrationSpec {

    @Autowired
    FollowersService followersService
    @Autowired
    WallService wallService
    @Autowired
    PostService postService
    @Autowired
    LikeService likeService
    @Autowired
    CommentService commentService

    def "get user wall"() {
        given:
        def username = "user1"
        def jwt1 = createTestUser(username)
        def userId1 = extractUserId(jwt1.token())

        def posts = (1..10).collect {
            postService.create(userId1, "some content")
        }

        posts.forEach(it -> {
            likeService.like(userId1, it.id())
            commentService.create(it.id(), userId1, "some")
        })

        when:
        def result = wallService.fetchUserPosts(userId1, 0)

        then:
        result.size() == 10
        result.get(0).postId() == posts.get(9).id()
        result.get(9).postId() == posts.get(0).id()

        result.stream().allMatch(it -> it.likesCount() == 1)
        result.stream().allMatch(it -> it.commentsCount() == 1)
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
        def result = wallService.fetchUserPosts(randomUUID(), 0)

        then:
        result.isEmpty()
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

            for (def j = 0; j < 2; j++) {
                def post = postService.create(followeeUserId, "some content $i $j")

                likeService.like(userId, post.id())
                commentService.create(post.id(), userId, "some")
            }

            followersService.follow(userId, followeeUserId)
        }

        when:
        def result = wallService.fetchWall(userId, 0)

        then:
        result.size() == 10

        result.get(0).content() == "some content 4 1"
        result.get(1).content() == "some content 4 0"
        result.get(8).content() == "some content 0 1"
        result.get(9).content() == "some content 0 0"

        result.stream().allMatch(it -> it.likesCount() == 1)
        result.stream().allMatch(it -> it.commentsCount() == 1)
    }

}