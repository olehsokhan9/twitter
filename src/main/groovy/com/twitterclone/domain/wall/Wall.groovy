package com.twitterclone.domain.wall

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.repository.query.Param

import static com.google.common.base.Preconditions.checkNotNull
import static com.google.common.base.Preconditions.checkState
import static com.twitterclone.domain.wall.WallCompositeKeyProvider.compositeId

@Document(collection = "walls")
@CompoundIndexes([
    @CompoundIndex(name = "walls_userId_createdDate_idx", def = "{'userId': 1, 'createdDate': -1}"),
    @CompoundIndex(name = "walls_userId_followeeId_idx", def = "{'userId': 1, 'followeeId': 1}")
])
class Wall {

    @Id
    @Field("id")
    private String id

    @Field("userId")
    private UUID userId // TODO: shard key

    @Field("postId")
    private UUID postId

    @Field("followeeId")
    private UUID followeeId

    @Field("createdDate")
    private Date createdDate

    Wall(@Param("id") String id,
         @Param("userId") UUID userId,
         @Param("postId") UUID postId,
         @Param("followeeId") UUID followeeId,
         @Param("createdDate") Date createdDate) {
        this.id = checkNotNull(id, "id")
        this.userId = checkNotNull(userId, "userId")
        this.postId = checkNotNull(postId, "postId")
        this.followeeId = checkNotNull(followeeId, "followeeId")
        this.createdDate = checkNotNull(createdDate, "createdDate")

        checkState(id == compositeId(userId, postId), "id should be a composite key")
    }

    Wall() {}

    String id() {
        return id
    }

    UUID userId() {
        return userId
    }

    UUID postId() {
        return postId
    }

    UUID followeeId() {
        return followeeId
    }

}
