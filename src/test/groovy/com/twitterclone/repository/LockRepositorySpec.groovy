package com.twitterclone.repository

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.domain.Lock
import org.springframework.beans.factory.annotation.Autowired

import static java.time.Instant.now

class LockRepositorySpec extends BaseIntegrationSpec {

    @Autowired
    LockRepository lockRepository

    def "should retrieve a Lock by id"() {
        given:
        def lock = new Lock(
            "test-lock-id",
            Date.from(now().plusSeconds(100))
        )
        lockRepository.save(lock)

        when:
        def result = lockRepository.getById("test-lock-id")

        then:
        result != null
        result.id() == "test-lock-id"
        result.expireAt() != null
    }
}