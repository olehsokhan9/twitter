package com.twitterclone.validations

import com.twitterclone.domain.Comment

import static com.google.common.base.Preconditions.*

class CommentValidation {

    private static final int CONTENT_MAX_LENGTH = 1000

    static void validate(Comment comment) {
        checkArgument(
            comment.content().size() < CONTENT_MAX_LENGTH && comment.content().size() > 0,
            "content length should be < %s and > 0, but %s".formatted(CONTENT_MAX_LENGTH, comment.content().size())
        )
    }
}
