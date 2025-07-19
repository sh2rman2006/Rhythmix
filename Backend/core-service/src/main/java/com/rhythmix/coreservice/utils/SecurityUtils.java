package com.rhythmix.coreservice.utils;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.security.Principal;
import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static UUID extractUserId(Principal principal) {
        if (!(principal instanceof JwtAuthenticationToken token)) {
            throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass().getName());
        }

        Jwt jwt = (Jwt) token.getPrincipal();
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid subject in JWT: " + jwt.getSubject(), ex);
        }
    }

}
