package com.twitterclone.dto.wall

import static com.google.common.base.Preconditions.checkNotNull
import static com.google.common.base.Preconditions.checkState

record WallPost(
    UUID postId,
    String content,
    long likesCount,
    long commentsCount
) {
    public WallPost {
        checkNotNull(postId, "postId")
        checkNotNull(content, "content")
        checkState(likesCount >= 0, "likes count should be >= 0, but %s".formatted(likesCount))
        checkState(commentsCount >= 0, "comments count should be >= 0, but %s".formatted(commentsCount))
    }
}