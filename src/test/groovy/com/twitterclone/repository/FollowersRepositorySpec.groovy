package com.twitterclone.repository

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.domain.Follower
import org.springframework.beans.factory.annotation.Autowired

import static java.util.UUID.randomUUID

class FollowersRepositorySpec extends BaseIntegrationSpec {

    @Autowired
    FollowersRepository followersRepository

    def "should retrieve a follower by followeeId and followerId"() {
        given:
        def followeeId = randomUUID()
        def followerId = randomUUID()
        def follower = new Follower(
            id: randomUUID(),
            followeeId: followeeId,
            followerId: followerId
        )
        followersRepository.save(follower)

        when:
        def result = followersRepository.getByFolloweeIdAndFollowerId(followeeId, followerId)

        then:
        result != null
        result.followeeId() == followeeId
        result.followerId() == followerId
    }

    def "should find a follower by followeeId and followerId"() {
        given:
        def followeeId = randomUUID()
        def followerId = randomUUID()
        def follower = new Follower(
            id: randomUUID(),
            followeeId: followeeId,
            followerId: followerId
        )
        followersRepository.save(follower)

        when:
        Optional<Follower> result = followersRepository.findByFolloweeIdAndFollowerId(followeeId, followerId)

        then:
        result.isPresent()
        result.get().followeeId() == followeeId
        result.get().followerId() == followerId
    }

    def "should return empty when follower is not found by followeeId and followerId"() {
        given:
        def followeeId = randomUUID()
        def followerId = randomUUID()

        when:
        Optional<Follower> result = followersRepository.findByFolloweeIdAndFollowerId(followeeId, followerId)

        then:
        !result.isPresent()
    }

    def "should retrieve followers by followeeId"() {
        given:
        def followeeId = randomUUID()
        def followers = (1..3).collect {
            new Follower(
                id: randomUUID(),
                followeeId: followeeId,
                followerId: randomUUID()
            )
        }
        followersRepository.saveAll(followers)

        when:
        def result = followersRepository.findAllByFolloweeId(followeeId)

        then:
        result.size() == 3
        result.every { it.followeeId() == followeeId }
    }
}