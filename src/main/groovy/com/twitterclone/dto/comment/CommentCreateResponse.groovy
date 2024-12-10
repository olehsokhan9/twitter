package com.twitterclone.dto.comment

import static com.google.common.base.Preconditions.checkNotNull

record CommentCreateResponse(
    UUID id
) {
    public CommentCreateResponse {
        checkNotNull(id, "id")
    }
}