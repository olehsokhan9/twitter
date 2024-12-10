package com.twitterclone.domain.post

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.repository.query.Param

import static com.google.common.base.Preconditions.checkNotNull
import static com.twitterclone.validations.PostValidation.validateContent
import static com.twitterclone.domain.post.Post.Builder.post

@Document(collection = "posts")
class Post {

    @Id
    @Field("id")
    private UUID id // TODO: shard key

    @Field("postedBy")
    private UUID postedBy

    @Field("content")
    private String content

    @Field("createdDate")
    private Date createdDate

    @Version
    @Field("version")
    private Long version

    Post(@Param("id") UUID id,
         @Param("postedBy") UUID postedBy,
         @Param("content") String content,
         @Param("createdDate") Date createdDate,
         @Param("version") Long version) {
        this.id = checkNotNull(id, "id")
        this.postedBy = checkNotNull(postedBy, "postedBy")
        this.content = checkNotNull(content, "content")
        this.createdDate = checkNotNull(createdDate, "createdDate")
        this.version = version

        validateContent(this)
    }

    Post() {}

    UUID id() {
        return id
    }

    UUID postedBy() {
        return postedBy
    }

    String content() {
        return content
    }

    Date createdDate() {
        return createdDate
    }

    long version() {
        return version
    }

    Post updateContent(String content) {
        return copy().content(content).build()
    }

    protected Builder copy() {
        return post()
            .id(this.id)
            .postedBy(this.postedBy)
            .content(this.content)
            .createdDate(this.createdDate)
            .version(this.version)
    }

    @Override
    String toString() {
        return new StringJoiner(", ", Post.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("postedBy=" + postedBy)
                .add("content='" + content + "'")
                .add("createdDate='" + createdDate + "'")
                .add("version=" + version)
                .toString();
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        Post post = (Post) o

        if (content != post.content) return false
        if (createdDate != post.createdDate) return false
        if (id != post.id) return false
        if (postedBy != post.postedBy) return false
        if (version != post.version) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (postedBy != null ? postedBy.hashCode() : 0)
        result = 31 * result + (content != null ? content.hashCode() : 0)
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        return result
    }

    static class Builder {
        private UUID id
        private UUID postedBy
        private String content
        private Date createdDate
        private Long version

        static Builder post() {
            return new Builder()
        }

        Post build() {
            return new Post(
                this.id,
                this.postedBy,
                this.content,
                this.createdDate,
                version
            )
        }

        Builder id(UUID id) {
            this.id = id
            return this
        }

        Builder postedBy(UUID postedBy) {
            this.postedBy = postedBy
            return this
        }

        Builder content(String content) {
            this.content = content
            return this
        }

        Builder createdDate(Date createdDate) {
            this.createdDate = createdDate
            return this
        }

        protected Builder version(Long version) {
            this.version = version
            return this
        }
    }
}
