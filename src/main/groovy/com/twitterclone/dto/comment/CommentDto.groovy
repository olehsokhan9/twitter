package com.twitterclone.dto.comment

import static com.google.common.base.Preconditions.checkNotNull

record CommentDto(
    UUID userId,
    String content
) {
    public CommentDto {
        checkNotNull(userId, "userId")
        checkNotNull(content, "content")
    }
}