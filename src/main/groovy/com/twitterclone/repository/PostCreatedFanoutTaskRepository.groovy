package com.twitterclone.repository

import com.twitterclone.domain.tasks.PostCreatedFanoutTask
import com.twitterclone.domain.tasks.PostCreatedFanoutTaskState
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PostCreatedFanoutTaskRepository extends MongoRepository<PostCreatedFanoutTask, UUID> {
    List<PostCreatedFanoutTask> findByStateAndProcessAtLessThan(PostCreatedFanoutTaskState state, Date processAt, Pageable pageable)
}