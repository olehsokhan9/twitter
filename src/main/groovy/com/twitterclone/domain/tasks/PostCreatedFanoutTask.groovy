package com.twitterclone.domain.tasks

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

import java.time.Duration

import static com.google.common.base.Preconditions.checkNotNull
import static com.twitterclone.domain.tasks.PostCreatedFanoutTaskState.IN_PROGRESS
import static java.time.Instant.now

@Document(collection = "post_created_fonout_tasks")
@CompoundIndexes([
    @CompoundIndex(name = "post_created_fonout_tasks_state_processAt_idx", def = "{'state': 1, 'processAt': 1}")
])
class PostCreatedFanoutTask {

    @Id
    @Field("postId")
    private UUID postId

    @Field("userId")
    private UUID userId

    @Field("state")
    private PostCreatedFanoutTaskState state

    @Field("processAt")
    @Indexed
    private Date processAt

    @Field("version")
    @Indexed
    private Long version

    PostCreatedFanoutTask(UUID postId, UUID userId, PostCreatedFanoutTaskState state, Date processAt, Long version) {
        this.postId = checkNotNull(postId)
        this.userId = checkNotNull(userId)
        this.state = checkNotNull(state)
        this.processAt = checkNotNull(processAt)
        this.version = version
    }

    PostCreatedFanoutTask(){}

    PostCreatedFanoutTask inProgress(Duration expectedProcessingTime) {
        return new PostCreatedFanoutTask(
            this.postId,
            this.userId,
            IN_PROGRESS,
            Date.from(now().plusMillis(expectedProcessingTime.toMillis())),
            this.version
        )
    }

    PostCreatedFanoutTaskState state() {
        return state
    }

    UUID postId() {
        return postId
    }

    UUID userId() {
        return userId
    }

    Date processAt() {
        return processAt
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        PostCreatedFanoutTask that = (PostCreatedFanoutTask) o

        if (postId != that.postId) return false
        if (processAt != that.processAt) return false
        if (state != that.state) return false
        if (userId != that.userId) return false

        return true
    }

    int hashCode() {
        int result
        result = (postId != null ? postId.hashCode() : 0)
        result = 31 * result + (userId != null ? userId.hashCode() : 0)
        result = 31 * result + (state != null ? state.hashCode() : 0)
        result = 31 * result + (processAt != null ? processAt.hashCode() : 0)
        return result
    }
}
