package com.twitterclone.repository

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.domain.LikeCounter
import org.springframework.beans.factory.annotation.Autowired

import static java.util.UUID.randomUUID

class LikeCounterRepositorySpec extends BaseIntegrationSpec {

    @Autowired
    LikeCounterRepository likeCounterRepository

    def "should retrieve a LikeCounter by postId"() {
        given:
        def postId = randomUUID()
        def likeCounter = new LikeCounter(
            postId: postId,
            count: 10
        )
        likeCounterRepository.save(likeCounter)

        when:
        def result = likeCounterRepository.getByPostId(postId)

        then:
        result != null
        result.postId() == postId
        result.count() == 10
    }

    def "should find a LikeCounter by postId"() {
        given:
        def postId = randomUUID()
        def likeCounter = new LikeCounter(
            postId: postId,
            count: 15
        )
        likeCounterRepository.save(likeCounter)

        when:
        Optional<LikeCounter> result = likeCounterRepository.findByPostId(postId)

        then:
        result.isPresent()
        result.get().postId() == postId
        result.get().count() == 15
    }

    def "should return empty when LikeCounter is not found by postId"() {
        given:
        def postId = randomUUID()

        when:
        Optional<LikeCounter> result = likeCounterRepository.findByPostId(postId)

        then:
        !result.isPresent()
    }

    def "should delete a LikeCounter by postId"() {
        given:
        def postId = randomUUID()
        def likeCounter = new LikeCounter(
            postId: postId,
            count: 20
        )
        likeCounterRepository.save(likeCounter)

        when:
        likeCounterRepository.deleteByPostId(postId)
        Optional<LikeCounter> result = likeCounterRepository.findByPostId(postId)

        then:
        !result.isPresent()
    }
}