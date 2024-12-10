package com.twitterclone.repository

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.domain.Comment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

import static java.util.UUID.randomUUID

class CommentRepositorySpec extends BaseIntegrationSpec {

    @Autowired
    CommentRepository commentRepository

    def "should retrieve a comments by id"() {
        given:
        def comment = new Comment(
            id: randomUUID(),
            postId: randomUUID(),
            userId: randomUUID(),
            content: "This is a test comment",
            version: null
        )
        commentRepository.save(comment)

        when:
        def result = commentRepository.getById(comment.id())

        then:
        result != null
        result.id() == comment.id()
        result.content() == "This is a test comment"
    }

    def "should retrieve comments by userId and postId"() {
        given:
        def userId = randomUUID()
        def postId = randomUUID()
        def comments = (1..3).collect {
            new Comment(
                id: randomUUID(),
                postId: postId,
                userId: userId,
                content: "Content $it",
                version: null
            )
        }
        commentRepository.saveAll(comments)

        when:
        def result = commentRepository.findByUserIdAndPostId(userId, postId)

        then:
        result.size() == 3
        result.every { it.userId() == userId && it.postId() == postId }
    }

    def "should retrieve paginated comments by postId"() {
        given:
        def postId = randomUUID()
        def comments = (1..5).collect {
            new Comment(
                    id: randomUUID(),
                    postId: postId,
                    userId: randomUUID(),
                    content: "Content $it",
                    version: null
            )
        }
        commentRepository.saveAll(comments)

        when:
        def pageable = PageRequest.of(0, 2)
        def result = commentRepository.findByPostIdWithPagination(postId, pageable)

        then:
        result.size() == 2
        result.every { it.postId() == postId }
    }
}