package com.twitterclone.repository

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.domain.Like
import org.springframework.beans.factory.annotation.Autowired

import static java.util.UUID.randomUUID

class LikeRepositorySpec extends BaseIntegrationSpec {

    @Autowired
    LikeRepository likeRepository

    def "should retrieve a like by ID"() {
        given:
        def like = new Like(
            id: randomUUID(),
            postId: randomUUID(),
            userId: randomUUID()
        )
        likeRepository.save(like)

        when:
        def result = likeRepository.getById(like.id())

        then:
        result != null
        result.id() == like.id()
        result.postId() == like.postId()
        result.userId() == like.userId()
    }

    def "should find a like by userId and postId"() {
        given:
        def userId = randomUUID()
        def postId = randomUUID()
        def like = new Like(
            id: randomUUID(),
            postId: postId,
            userId: userId
        )
        likeRepository.save(like)

        when:
        Optional<Like> result = likeRepository.findByUserIdAndPostId(userId, postId)

        then:
        result.isPresent()
        result.get().id() == like.id()
        result.get().postId() == postId
        result.get().userId() == userId
    }

    def "should return empty when like is not found by userId and postId"() {
        given:
        def userId = randomUUID()
        def postId = randomUUID()

        when:
        Optional<Like> result = likeRepository.findByUserIdAndPostId(userId, postId)

        then:
        !result.isPresent()
    }
}