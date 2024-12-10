package com.twitterclone.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.repository.query.Param

import static com.google.common.base.Preconditions.checkNotNull
import static com.twitterclone.validations.CommentValidation.validate

@Document(collection = "posts")
class Comment {

    @Id
    @Field("id")
    private UUID id

    @Field("postId")
    @Indexed
    private UUID postId // TODO: shard key

    @Field("userId")
    private UUID userId

    @Field("content")
    private String content

    @Version
    @Field("version")
    private Long version

    Comment(@Param("id") UUID id,
            @Param("postId") UUID postId,
            @Param("userId") UUID userId,
            @Param("content") String content,
            @Param("version") Long version) {
        this.id = checkNotNull(id, "id")
        this.postId = checkNotNull(postId, "postId")
        this.userId = checkNotNull(userId, "userId")
        this.content = checkNotNull(content, "content")
        this.version = version

        validate(this)
    }

    Comment() {}

    UUID id() {
        return id
    }

    UUID postId() {
        return postId
    }

    UUID userId() {
        return userId
    }

    String content() {
        return content
    }

    long version() {
        return version
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        Comment comment = (Comment) o

        if (content != comment.content) return false
        if (id != comment.id) return false
        if (postId != comment.postId) return false
        if (userId != comment.userId) return false
        if (version != comment.version) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (postId != null ? postId.hashCode() : 0)
        result = 31 * result + (userId != null ? userId.hashCode() : 0)
        result = 31 * result + (content != null ? content.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        return result
    }

    static class Builder {

        private UUID id
        private UUID postId
        private UUID userId
        private String content
        private Long version

        static Builder comment() {
            return new Builder()
        }

        Comment build() {
            return new Comment(
                this.id,
                this.postId,
                this.userId,
                this.content,
                this.version
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

        Builder content(String content) {
            this.content = content
            return this
        }

        protected Builder version(Long version) {
            this.version = version
            return this
        }
    }
}
