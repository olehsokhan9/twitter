package com.twitterclone.domain.post

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.repository.query.Param

import static com.google.common.base.Preconditions.checkNotNull

@Document(collection = "posts_by_user")
@CompoundIndexes([
    @CompoundIndex(name = "posts_by_user_postedBy_createdDate_idx", def = "{'postedBy': 1, 'createdDate': -1}")
])
class PostShardByUser {

    @Id
    @Field("id")
    private UUID id

    @Field("postId")
    private UUID postId

    @Field("postedBy")
    private UUID postedBy // TODO: shard key

    @Field("createdDate")
    private Date createdDate

    PostShardByUser(@Param("id") UUID id,
                    @Param("postedBy") UUID postedBy,
                    @Param("postId") UUID postId,
                    @Param("createdDate") Date createdDate) {
        this.id = checkNotNull(id, "id")
        this.postedBy = checkNotNull(postedBy, "postedBy")
        this.postId = checkNotNull(postId, "postId")
        this.createdDate = checkNotNull(createdDate, "createdDate")
    }

    PostShardByUser() {}

    UUID postedBy() {
        return postedBy
    }

    UUID postId() {
        return postId
    }

    Date createdDate() {
        return createdDate
    }
}
