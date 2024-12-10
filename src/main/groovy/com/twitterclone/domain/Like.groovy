package com.twitterclone.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.repository.query.Param

import static com.google.common.base.Preconditions.checkNotNull

@Document(collection = "likes")
@CompoundIndexes([
    @CompoundIndex(name = "likes_postId_userId_idx", def = "{'postId': 1, 'userId': 1}", unique = true)
])
class Like {

    @Id
    @Field("id")
    private UUID id

    @Field("postId")
    private UUID postId // TODO: shard key

    @Field("userId")
    private UUID userId

    Like(@Param("id") UUID id,
         @Param("postId") UUID postId,
         @Param("userId") UUID userId) {
        this.id = checkNotNull(id, "id")
        this.postId = checkNotNull(postId, "postId")
        this.userId = checkNotNull(userId, "userId")
    }

    Like() {}

    UUID id() {
        return id
    }

    UUID postId() {
        return postId
    }

    UUID userId() {
        return userId
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        Like like = (Like) o

        if (id != like.id) return false
        if (postId != like.postId) return false
        if (userId != like.userId) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (postId != null ? postId.hashCode() : 0)
        result = 31 * result + (userId != null ? userId.hashCode() : 0)
        return result
    }

    static class Builder {

        private UUID id
        private UUID postId
        private UUID userId

        static Builder like() {
            return new Builder()
        }

        Like build() {
            return new Like(
                this.id,
                this.postId,
                this.userId
            )
        }

        Builder id(UUID id) {
            this.id = id
            return this
        }

        Builder postId(UUID postId) {
            this.postId = postId
            return this
        }

        Builder userId(UUID userId) {
            this.userId = userId
            return this
        }
    }
}
