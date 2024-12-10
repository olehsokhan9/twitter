package com.twitterclone.domain.wall

class WallCompositeKeyProvider {

    static String compositeId(UUID userId, UUID postId) {
        return userId.toString() + "|" + postId.toString()
    }

}
