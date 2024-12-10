package com.twitterclone.service

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.repository.FollowersRepository
import com.twitterclone.repository.WallRepository
import org.springframework.beans.factory.annotation.Autowired

class FollowersServiceSpec extends BaseIntegrationSpec {

    @Autowired
    FollowersService followersService
    @Autowired
    WallRepository wallRepository
    @Autowired
    PostService postService
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
        followersService.follow(userId1, userId2)

        then:
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
        followersService.unfollow(userId1, userId2)

        then:
        followersRepository.findByFolloweeIdAndFollowerId(userId2, userId1).isEmpty()
    }

    def "follow user create posts for wall"() {
        given:
        def username1 = "user1"
        def username2 = "user2"
        def jwt1 = createTestUser(username1)
        def jwt2 = createTestUser(username2)

        def userId1 = extractUserId(jwt1.token())
        def userId2 = extractUserId(jwt2.token())

        def post1 = postService.create(userId2, "post 1")
        def post2 = postService.create(userId2, "post 2")

        when:
        followersService.follow(userId1, userId2)

        then:
        def follower = followersRepository.getByFolloweeIdAndFollowerId(userId2, userId1)
        follower.followeeId() == userId2
        follower.followerId() == userId1

        def posts = wallRepository.findByUserId(userId1)
        posts.size() == 2
        posts.get(0).postId() == post2.id()
        posts.get(1).postId() == post1.id()
    }

    def "unfollow user delete followee posts from wall"() {
        given:
        def username1 = "user1"
        def username2 = "user2"
        def jwt1 = createTestUser(username1)
        def jwt2 = createTestUser(username2)

        def userId1 = extractUserId(jwt1.token())
        def userId2 = extractUserId(jwt2.token())

        postService.create(userId2, "post 1")
        postService.create(userId2, "post 2")

        followersService.follow(userId1, userId2)

        when:
        followersService.unfollow(userId1, userId2)

        then:
        followersRepository.findByFolloweeIdAndFollowerId(userId2, userId1).isEmpty()
        wallRepository.findByUserId(userId1).isEmpty()
    }
}