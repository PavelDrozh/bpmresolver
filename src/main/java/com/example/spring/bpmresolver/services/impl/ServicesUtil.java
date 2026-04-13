package com.example.spring.bpmresolver.services.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.function.Function;

public class ServicesUtil {
    
    private ServicesUtil() {
        /* This utility class should not be instantiated */
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    public static <T> String getFromDb(Optional<T> optionalSetting, Function<T, String> getter) {
        if (optionalSetting.isEmpty()) {
            return null;
        }
        return optionalSetting
                .map(getter)
                .map(ServicesUtil::normalize)
                .orElse(null);
    }

    public static String getCurrentUsernameOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        if (principal instanceof String s && !s.isBlank() && !"anonymousUser".equalsIgnoreCase(s)) {
            return s;
        }
        return null;
    }
}
