package com.twitterclone.repository

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.domain.post.PostShardByUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

import static java.util.UUID.randomUUID

class PostShardByUserRepositorySpec extends BaseIntegrationSpec {

    @Autowired
    PostShardByUserRepository postShardByUserRepository

    def "should retrieve a postShardByUser by postedBy and postId"() {
        given:
        def userId = randomUUID()
        def postId = randomUUID()
        def postShard = new PostShardByUser(
            id: randomUUID(),
            postedBy: userId,
            postId: postId,
            createdDate: new Date()
        )
        postShardByUserRepository.save(postShard)

        when:
        def result = postShardByUserRepository.getByPostedByAndPostId(userId, postId)

        then:
        result != null
        result.postedBy() == userId
        result.postId() == postId
    }

    def "should find a postShardByUser by postedBy and postId"() {
        given:
        def userId = randomUUID()
        def postId = randomUUID()
        def postShard = new PostShardByUser(
            id: randomUUID(),
            postedBy: userId,
            postId: postId,
            createdDate: new Date()
        )
        postShardByUserRepository.save(postShard)

        when:
        Optional<PostShardByUser> result = postShardByUserRepository.findByPostedByAndPostId(userId, postId)

        then:
        result.isPresent()
        result.get().postedBy() == userId
        result.get().postId() == postId
    }

    def "should retrieve paginated postShardByUser by postedBy"() {
        given:
        def userId = randomUUID()
        def posts = (1..5).collect {
            new PostShardByUser(
                id: randomUUID(),
                postedBy: userId,
                postId: randomUUID(),
                createdDate: new Date()
            )
        }
        postShardByUserRepository.saveAll(posts)

        when:
        def pageable = PageRequest.of(0, 3) // First page, 3 items per page
        def result = postShardByUserRepository.findByPostedBy(userId, pageable)

        then:
        result.size() == 3
        result.every { it.postedBy() == userId }
    }

    def "should delete a postShardByUser by postedBy and postId"() {
        given:
        def userId = randomUUID()
        def postId = randomUUID()
        def postShard = new PostShardByUser(
            id: randomUUID(),
            postedBy: userId,
            postId: postId,
            createdDate: new Date()
        )
        postShardByUserRepository.save(postShard)

        when:
        postShardByUserRepository.deleteByPostedByAndPostId(userId, postId)
        Optional<PostShardByUser> result = postShardByUserRepository.findByPostedByAndPostId(userId, postId)

        then:
        !result.isPresent()
    }
}