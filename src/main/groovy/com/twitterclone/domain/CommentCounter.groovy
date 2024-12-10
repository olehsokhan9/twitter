package com.twitterclone.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.repository.query.Param

import static com.google.common.base.Preconditions.checkNotNull
import static com.google.common.base.Preconditions.checkState

@Document(collection = "comments_counters")
class CommentCounter {

    @Id
    @Field("postId")
    private UUID postId // TODO: shard key

    @Field("userId")
    private long count

    CommentCounter(@Param("postId") UUID postId,
                   @Param("userId") long count) {
        this.postId = checkNotNull(postId, "postId")
        this.count = count

        checkState(count >= 0, "count should be >= 0, but %s".formatted(count))
    }

    CommentCounter() {}

    UUID postId() {
        return postId
    }

    long count() {
        return count
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        CommentCounter that = (CommentCounter) o

        if (count != that.count) return false
        if (postId != that.postId) return false

        return true
    }

    int hashCode() {
        int result
        result = (postId != null ? postId.hashCode() : 0)
        result = 31 * result + (int) (count ^ (count >>> 32))
        return result
    }

    static class Builder {

        private UUID postId
        private long count

        static Builder commentCounter() {
            return new Builder()
        }

        CommentCounter build() {
            return new CommentCounter(
                this.postId,
                this.count
            )
        }

        Builder postId(UUID postId) {
            this.postId = postId
            return this
        }

        Builder count(long count) {
            this.count = count
            return this
        }
    }
}
