package com.claimswift.document.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiResponse<T> {

    private LocalDateTime timestamp;
    private boolean success;
    private T data;
    private String message;

    // ===============================
    // SUCCESS RESPONSE
    // ===============================
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .data(data)
                .message("Request successful")
                .build();
    }

    // ===============================
    // SUCCESS WITH CUSTOM MESSAGE
    // ===============================
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .data(data)
                .message(message)
                .build();
    }

    // ===============================
    // ERROR RESPONSE
    // ===============================
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(false)
                .data(null)
                .message(message)
                .build();
    }
}