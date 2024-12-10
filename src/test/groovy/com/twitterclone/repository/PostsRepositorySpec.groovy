package com.twitterclone.repository

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.domain.post.Post
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

import static java.util.UUID.randomUUID

class PostsRepositorySpec extends BaseIntegrationSpec {

    @Autowired
    PostRepository postRepository

    def "should retrieve a post by id"() {
        given:
        def post = new Post(
            id: randomUUID(),
            postedBy: randomUUID(),
            content: "This is a test post",
            createdDate: new Date(),
            version: null
        )
        postRepository.save(post)

        when:
        def result = postRepository.getById(post.id())

        then:
        result != null
        result.id() == post.id()
        result.content() == "This is a test post"
    }

    def "should retrieve all posts by postedBy"() {
        given:
        def postedBy = randomUUID()
        def posts = (1..3).collect {
            new Post(
                id: randomUUID(),
                postedBy: postedBy,
                content: "Content $it",
                createdDate: new Date(),
                version: null
            )
        }
        postRepository.saveAll(posts)

        when:
        def result = postRepository.findAllByPostedBy(postedBy)

        then:
        result.size() == 3
        result.every { it.postedBy() == postedBy }
    }

    def "should retrieve paginated Posts by postedBy"() {
        given:
        def postedBy = randomUUID()
        def posts = (1..5).collect {
            new Post(
                id: randomUUID(),
                postedBy: postedBy,
                content: "Content $it",
                createdDate: new Date(),
                version: null
            )
        }
        postRepository.saveAll(posts)

        when:
        def pageable = PageRequest.of(0, 2)
        def result = postRepository.findByPostedBy(postedBy, pageable)

        then:
        result.size() == 2
        result.every { it.postedBy() == postedBy }
    }
}