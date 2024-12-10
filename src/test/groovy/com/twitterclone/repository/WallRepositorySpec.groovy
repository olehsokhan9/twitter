package com.twitterclone.repository

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.domain.wall.Wall
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

import static com.twitterclone.domain.wall.WallCompositeKeyProvider.compositeId
import static java.util.UUID.randomUUID

class WallRepositorySpec extends BaseIntegrationSpec {

    @Autowired
    WallRepository wallRepository

    def "should retrieve a wall by id"() {
        given:
        def userId = randomUUID()
        def postId = randomUUID()
        def wall = new Wall(
            id: compositeId(userId, postId),
            userId: userId,
            postId: postId,
            followeeId: randomUUID(),
            createdDate: new Date()
        )
        wallRepository.save(wall)

        when:
        def result = wallRepository.getById(wall.id())

        then:
        result != null
        result.id() == wall.id()
    }

    def "should find walls by userId with pagination"() {
        given:
        def userId = randomUUID()
        def walls = (1..5).collect {
            def postId = randomUUID()
            new Wall(
                id: compositeId(userId, postId),
                userId: userId,
                postId: postId,
                followeeId: randomUUID(),
                createdDate: new Date()
            )
        }
        wallRepository.saveAll(walls)

        when:
        def pageable = PageRequest.of(0, 3)
        def result = wallRepository.findByUserId(userId, pageable)

        then:
        result.size() == 3
        result.every { it.userId() == userId }
    }

    def "should delete walls by userId and followeeId"() {
        given:
        def userId = randomUUID()
        def postId = randomUUID()
        def followeeId = randomUUID()
        def walls = (1..3).collect {
            new Wall(
                id: compositeId(userId, postId),
                userId: userId,
                postId: postId,
                followeeId: followeeId,
                createdDate: new Date()
            )
        }
        wallRepository.saveAll(walls)

        when:
        wallRepository.deleteAllByUserIdAndFolloweeId(userId, followeeId)

        then:
        wallRepository.findByUserId(userId, PageRequest.of(0, 10)).isEmpty()
    }
}