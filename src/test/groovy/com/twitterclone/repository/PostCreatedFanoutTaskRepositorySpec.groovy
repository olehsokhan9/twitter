package com.twitterclone.repository

import com.twitterclone.BaseIntegrationSpec
import com.twitterclone.domain.tasks.PostCreatedFanoutTask
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

import java.time.Instant

import static com.twitterclone.domain.tasks.PostCreatedFanoutTaskState.CREATED
import static com.twitterclone.domain.tasks.PostCreatedFanoutTaskState.IN_PROGRESS
import static java.time.Instant.now
import static java.util.UUID.randomUUID

class PostCreatedFanoutTaskRepositorySpec extends BaseIntegrationSpec {

    @Autowired
    PostCreatedFanoutTaskRepository postCreatedFanoutTaskRepository

    def "should retrieve tasks by state and processAt less than a specified time"() {
        given:
        def now = Date.from(now())
        def futureDate = Date.from(Instant.now().plusSeconds(3600))
        def pastDate = Date.from(Instant.now().minusSeconds(3600))
        def tasks = [
                new PostCreatedFanoutTask(randomUUID(), randomUUID(), CREATED, pastDate, null),
                new PostCreatedFanoutTask(randomUUID(), randomUUID(), CREATED, futureDate, null),
                new PostCreatedFanoutTask(randomUUID(), randomUUID(), IN_PROGRESS, pastDate, null)
        ]
        postCreatedFanoutTaskRepository.saveAll(tasks)

        when:
        def pageable = PageRequest.of(0, 10)
        def result = postCreatedFanoutTaskRepository.findByStateAndProcessAtLessThan(CREATED, now, pageable)

        then:
        result.size() == 1
        result[0].state() == CREATED
        result[0].processAt().before(now)
    }

    def "should return empty list if no tasks match the criteria"() {
        given:
        def futureDate = Date.from(now().plusSeconds(3600))
        def tasks = [
            new PostCreatedFanoutTask(randomUUID(), randomUUID(), IN_PROGRESS, futureDate, null)
        ]
        postCreatedFanoutTaskRepository.saveAll(tasks)

        when:
        def pageable = PageRequest.of(0, 10)
        def result = postCreatedFanoutTaskRepository.findByStateAndProcessAtLessThan(CREATED, Date.from(now().minusSeconds(3600)), pageable)

        then:
        result.isEmpty()
    }
}