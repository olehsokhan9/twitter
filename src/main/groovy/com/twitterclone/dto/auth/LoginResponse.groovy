package com.twitterclone.dto.auth

import static com.google.common.base.Preconditions.checkNotNull
import static com.google.common.base.Preconditions.checkState

record LoginResponse(
    String token
) {
    public LoginResponse {
        checkNotNull(token)
        checkState(!token.isEmpty())
    }
}
