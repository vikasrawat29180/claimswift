package com.claimswift.document.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("User not authenticated");
        }

        try {
            // Try to parse as Long first (for backward compatibility)
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            // If it's a username string, return a default or hash it
            // For now, return a placeholder - in production, you might want to 
            // look up the user ID from a user service
            return 1L; // Default admin ID
        }
    }
    
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("User not authenticated");
        }

        return authentication.getName();
    }
}
