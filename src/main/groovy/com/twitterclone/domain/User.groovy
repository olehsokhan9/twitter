package com.twitterclone.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.repository.query.Param

import static com.google.common.base.Preconditions.checkNotNull
import static com.twitterclone.validations.UserValidation.validate

@Document(collection = "users")
class User {

    @Id
    @Field("id")
    private UUID id // TODO: shard key

    @Field("username")
    @Indexed(unique = true)
    private String username

    @Field("encodedPassword")
    private String encodedPassword

    @Version
    @Field("version")
    private Long version

    User(@Param("id") UUID id,
         @Param("username") String username,
         @Param("encodedPassword") String encodedPassword,
         @Param("version") Long version) {
        this.id = checkNotNull(id, "id")
        this.username = checkNotNull(username, "username")
        this.encodedPassword = checkNotNull(encodedPassword, "encodedPassword")
        this.version = version

        validate(this)
    }

    User() {}

    UUID id() {
        return id
    }

    String username() {
        return username
    }

    long version() {
        return version
    }

    String encodedPassword() {
        return encodedPassword
    }

    User updateUsername(String username) {
        return copy().username(username).build()
    }

    protected Builder copy() {
        return Builder.user()
            .id(this.id)
            .username(this.username)
            .encodedPassword(this.encodedPassword)
            .version(this.version)
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        User user = (User) o

        if (encodedPassword != user.encodedPassword()) return false
        if (id != user.id()) return false
        if (username != user.username) return false
        if (version != user.version) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (username != null ? username.hashCode() : 0)
        result = 31 * result + (encodedPassword != null ? encodedPassword.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        return result
    }

    @Override
    String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("username='***'")
            .add("encodedPassword='***'")
            .toString()
    }

    static class Builder {
        private UUID id
        private String username
        private String encodedPassword
        private Long version

        static Builder user() {
            return new Builder()
        }

        User build() {
            return new User(
                this.id,
                this.username,
                this.encodedPassword,
                version
            )
        }

        Builder id(UUID id) {
            this.id = id
            return this
        }

        Builder username(String username) {
            this.username = username
            return this
        }

        Builder encodedPassword(String encodedPassword) {
            this.encodedPassword = encodedPassword
            return this
        }

        protected Builder version(Long version) {
            this.version = version
            return this
        }
    }
}
