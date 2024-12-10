package com.twitterclone.validations

import com.twitterclone.domain.post.Post
import com.twitterclone.exceptions.InvalidContentException
import com.twitterclone.exceptions.PostNotFoundException

import static com.google.common.base.Preconditions.*

class PostValidation {

    private static final int CONTENT_MAX_LENGTH = 1000

    static void validateContent(Post post) {
        checkArgument(
                post.content().size() < CONTENT_MAX_LENGTH && post.content().size() > 0,
                "content length should be < %s and > 0, but %s".formatted(CONTENT_MAX_LENGTH, post.content().size())
        )
    }

    static void validateContent(String content) {
        try {
            checkNotNull(content)
            checkState(content.length() > 0 && content.length() < CONTENT_MAX_LENGTH)
        } catch (Exception ignored) {
            throw new InvalidContentException()
        }
    }

    static void validatePostCreatedBy(Optional<Post> post, UUID userId) {
        if (post.isEmpty())
            throw new PostNotFoundException()
        if (post.orElseThrow().postedBy() != userId)
            throw new PostNotFoundException()
    }
}
