package com.phoneshop.auditing;


import com.phoneshop.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    private final HttpServletRequest request;

    public AuditorAwareImpl(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        String username = null;

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            username = userDetails.getUsername();
        } else if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            Object usernameClaim = jwt.getClaim("username");
            if (usernameClaim != null) {
                username = usernameClaim.toString();
            }
        }

        String ip = (request != null) ? request.getRemoteAddr() : "N/A";

        return (username != null) ? Optional.of(username) : Optional.empty();
    }
}
