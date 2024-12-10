package com.twitterclone.repository

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.domain.CommentCounter
import org.springframework.beans.factory.annotation.Autowired

import static java.util.UUID.randomUUID

class CommentCounterRepositorySpec extends BaseIntegrationSpec {

    @Autowired
    CommentCounterRepository commentCounterRepository

    def "should retrieve a commentCounter by postId"() {
        given:
        def postId = randomUUID()
        def commentCounter = new CommentCounter(
            postId: postId,
            count: 10
        )
        commentCounterRepository.save(commentCounter)

        when:
        def result = commentCounterRepository.getByPostId(postId)

        then:
        result != null
        result.postId() == postId
        result.count() == 10
    }

    def "should find a commentCounter by postId"() {
        given:
        def postId = randomUUID()
        def commentCounter = new CommentCounter(
            postId: postId,
            count: 15
        )
        commentCounterRepository.save(commentCounter)

        when:
        Optional<CommentCounter> result = commentCounterRepository.findByPostId(postId)

        then:
        result.isPresent()
        result.get().postId() == postId
        result.get().count() == 15
    }

    def "should return empty when commentCounter is not found by postId"() {
        given:
        def postId = randomUUID()

        when:
        Optional<CommentCounter> result = commentCounterRepository.findByPostId(postId)

        then:
        !result.isPresent()
    }

    def "should delete a commentCounter by postId"() {
        given:
        def postId = randomUUID()
        def commentCounter = new CommentCounter(
            postId: postId,
            count: 20
        )
        commentCounterRepository.save(commentCounter)

        when:
        commentCounterRepository.deleteByPostId(postId)
        Optional<CommentCounter> result = commentCounterRepository.findByPostId(postId)

        then:
        !result.isPresent()
    }
}