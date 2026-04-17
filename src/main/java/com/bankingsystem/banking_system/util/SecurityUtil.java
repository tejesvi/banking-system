package com.bankingsystem.banking_system.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

public class SecurityUtil {

    public static String getCurrentUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    public static boolean hasRole(String role) {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }
}
