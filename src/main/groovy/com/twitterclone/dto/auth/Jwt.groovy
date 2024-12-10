package com.twitterclone.dto.auth

import static com.google.common.base.Preconditions.checkNotNull
import static com.google.common.base.Preconditions.checkState

record Jwt(
    String token
) {
    public Jwt {
        checkNotNull(token)
        checkState(!token.isEmpty())
    }

    @Override
    String toString() {
        return new StringJoiner(", ", Jwt.class.getSimpleName() + "[", "]")
                .add("token='***'")
                .toString();
    }
}