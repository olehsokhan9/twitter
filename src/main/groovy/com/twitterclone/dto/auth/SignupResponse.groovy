package com.twitterclone.dto.auth

import static com.google.common.base.Preconditions.checkNotNull
import static com.google.common.base.Preconditions.checkState

record SignupResponse(
    String token
) {
    public SignupResponse {
        checkNotNull(token)
        checkState(!token.isEmpty())
    }
}
