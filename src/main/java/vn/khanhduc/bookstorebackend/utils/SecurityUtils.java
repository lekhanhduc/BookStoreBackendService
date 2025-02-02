package vn.khanhduc.bookstorebackend.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Optional;

public class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<String> getCurrentLogin() {
        SecurityContext contextHolder = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(contextHolder.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if(authentication == null) return null;

        if(authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        else if(authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        else if (authentication.getPrincipal() instanceof  String s) {
            return s;
        }
        return null;
    }
}
