package com.twitterclone.controllers

import com.twitterclone.dto.wall.WallPost
import com.twitterclone.service.WallService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/wall")
class WallController extends AbstractBaseController {

    private final WallService wallService

    WallController(WallService wallService) {
        this.wallService = wallService
    }

    @GetMapping("/{userId}")
    List<WallPost> fetchUserPosts(@PathVariable("userId") UUID userId, @RequestParam(value = "p", defaultValue = "0") int p) {
        return wallService.fetchUserPosts(userId, p)
    }

    @GetMapping("/following")
    List<WallPost> fetchFollowingWall(@RequestParam(value = "p", defaultValue = "0") int p) {
        final def userId = getUserId()
        return wallService.fetchWall(userId, p)
    }
}
