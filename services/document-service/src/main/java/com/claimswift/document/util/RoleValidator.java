package com.claimswift.document.util;

import com.claimswift.document.exception.FileValidationException;

public class RoleValidator {

    public static void validateUploadRole(String role) {
        if (!"ROLE_POLICYHOLDER".equals(role)) {
            throw new FileValidationException("Only POLICYHOLDER can upload documents");
        }
    }

    public static void validateDownloadRole(String role) {
        if (!role.equals("ROLE_ADJUSTER") && !role.equals("ROLE_MANAGER")) {
            throw new FileValidationException("Only ADJUSTER or MANAGER can download");
        }
    }
}