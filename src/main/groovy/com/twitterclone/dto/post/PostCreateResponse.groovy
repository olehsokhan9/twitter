package com.twitterclone.dto.post

import static com.google.common.base.Preconditions.checkNotNull

record PostCreateResponse(
    UUID id
) {
    public PostCreateResponse {
        checkNotNull(id, "id")
    }
}