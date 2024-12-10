package com.twitterclone.configs

import com.twitterclone.repository.UserRepository
import com.twitterclone.service.UserService
import com.twitterclone.utils.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil
    private final UserService userService

    JwtAuthenticationFilter(JwtUtil jwtUtil,
                            UserService userService) {
        this.jwtUtil = jwtUtil
        this.userService = userService
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final def authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final def jwt = authHeader.substring(7)
            final def userId = jwtUtil.extractUserId(jwt)

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtUtil.validateToken(jwt) && userService.userExists(userId)) {
                    final def authToken = new UsernamePasswordAuthenticationToken(userId, null, [])
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request))
                    SecurityContextHolder.getContext().setAuthentication(authToken)
                }
            }
        }

        filterChain.doFilter(request, response)
    }
}