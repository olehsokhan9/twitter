package com.twitterclone.dto.comment

import static com.google.common.base.Preconditions.checkNotNull

record ListCommentsResponse(
    List<CommentDto> comments
) {
    public ListCommentsResponse {
        checkNotNull(comments, "comments")
    }
}