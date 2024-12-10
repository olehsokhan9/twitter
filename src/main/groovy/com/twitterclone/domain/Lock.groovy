package com.twitterclone.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "locks")
class Lock {
    @Id
    private String id

    @Field("expireAt")
    @Indexed(expireAfter = "0")
    private Date expireAt

    Lock(){}

    Lock(String id, Date expireAt) {
        this.id = id
        this.expireAt = expireAt
    }

    String id() {
        return id
    }

    Date expireAt() {
        return expireAt
    }
}
