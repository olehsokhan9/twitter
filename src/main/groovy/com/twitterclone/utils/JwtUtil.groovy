package com.twitterclone.utils

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import java.security.Key
import java.time.Instant

import static io.jsonwebtoken.SignatureAlgorithm.HS256
import static java.time.temporal.ChronoUnit.HOURS

@Component
class JwtUtil {

    // todo move to vault
    @Value('${jwt.secret}')
    private String secret

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes())
    }

    String generateToken(UUID userId) {
        final def now = Instant.now()
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, HOURS)))
                .signWith(getSigningKey(), HS256)
                .compact()
    }

    UUID extractUserId(String token) {
        final def userId = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject()
        return UUID.fromString(userId)
    }

    boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
            return true
        } catch (Exception e) {
            return false
        }
    }

}
