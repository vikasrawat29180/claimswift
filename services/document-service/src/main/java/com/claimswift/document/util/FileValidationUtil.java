package com.claimswift.document.util;

import com.claimswift.document.exception.FileValidationException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileValidationUtil {

    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "image/jpeg",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
    );

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB//why *

    public static void validate(MultipartFile file) {

        if (file.isEmpty()) {
            throw new FileValidationException("File is empty");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new FileValidationException("Only PDF, JPG, Word, TXT files are allowed");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new FileValidationException("File size exceeds 5MB");
        }
    }
}