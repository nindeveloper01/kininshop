// java
    package com.phoneshop.utils;

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.oauth2.jwt.Jwt;

    import java.util.UUID;

    public class AuthUtils {
        private static final Logger log = LoggerFactory.getLogger(AuthUtils.class);

        public static UUID getCurrentUserId() {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                log.debug("No authentication in security context");
                return null;
            }

            Object principal = auth.getPrincipal();
            String userIdStr = null;

            if (principal instanceof Jwt) {
                Jwt jwt = (Jwt) principal;
                Object claim = jwt.getClaim("userId");
                userIdStr = claim != null ? String.valueOf(claim) : null;
            } else if (principal instanceof UserDetails) {
                // No dedicated userId claim available; fallback to username if it encodes an id
                userIdStr = auth.getName();
            } else {
                userIdStr = auth.getName();
            }

            if (userIdStr == null) {
                log.debug("userId claim not found");
                return null;
            }

            try {
                return UUID.fromString(userIdStr);
            } catch (IllegalArgumentException ex) {
                log.warn("Failed to parse userId '{}' as UUID", userIdStr);
                return null;
            }
        }

        public static String getCurrentUsername() {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                log.debug("No authentication in security context");
                return null;
            }

            Object principal = auth.getPrincipal();
            String username = null;

            if (principal instanceof Jwt) {
                Jwt jwt = (Jwt) principal;
                username = jwt.getClaim("username") != null ? String.valueOf(jwt.getClaim("username")) : null;
                if (username == null) {
                    username = jwt.getClaim("email") != null ? String.valueOf(jwt.getClaim("email")) : null;
                }
                if (username == null) {
                    username = jwt.getSubject();
                }
            } else if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            }

            if (username == null) {
                username = auth.getName();
            }

            log.info("Current username: {}", username);
            return username;
        }
    }