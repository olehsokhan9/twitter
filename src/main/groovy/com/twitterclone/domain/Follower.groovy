package com.twitterclone.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.repository.query.Param

import static com.google.common.base.Preconditions.checkNotNull
import static com.google.common.base.Preconditions.checkState

@Document(collection = "followers")
@CompoundIndexes([
    @CompoundIndex(name = "followers_followeeId_followerId", def = "{'followeeId': 1, 'followerId': 1}", unique = true)
])
class Follower {

    @Id
    @Field("id")
    private UUID id

    @Field("followeeId")
    private UUID followeeId  // TODO: shard key

    @Field("followerId")
    private UUID followerId

    Follower(@Param("id") UUID id,
             @Param("followeeId") UUID followeeId,
             @Param("followerId") UUID followerId) {
        this.id = checkNotNull(id, "id")
        this.followeeId = checkNotNull(followeeId, "followeeId")
        this.followerId = checkNotNull(followerId, "followerId")

        checkState(followeeId != followerId, "followeeId and followerId must be not the same userId")
    }

    Follower() {}

    UUID id() {
        return id
    }

    UUID followeeId() {
        return followeeId
    }

    UUID followerId() {
        return followerId
    }

    static class Builder {

        private UUID id
        private UUID followeeId
        private UUID followerId

        static Builder follower() {
            return new Builder()
        }

        Follower build() {
            return new Follower(
                this.id,
                this.followeeId,
                this.followerId
            )
        }

        Builder id(UUID id) {
            this.id = id
            return this
        }

        Builder followeeId(UUID followeeId) {
            this.followeeId = followeeId
            return this
        }

        Builder followerId(UUID followerId) {
            this.followerId = followerId
            return this
        }
    }
}
