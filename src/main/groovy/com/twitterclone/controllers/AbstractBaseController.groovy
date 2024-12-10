package com.twitterclone.controllers

import org.springframework.security.core.context.SecurityContextHolder

abstract class AbstractBaseController {

    protected static UUID getUserId() {
        final def authentication = SecurityContextHolder.getContext().getAuthentication()
        return  (UUID) authentication.getPrincipal()
    }

}
