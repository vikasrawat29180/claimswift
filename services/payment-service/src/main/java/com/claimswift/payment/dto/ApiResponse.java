package com.claimswift.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Standard API Response Structure
 * 
 * Every service must return responses in this structure:
 * {
 *   "success": true,
 *   "timestamp": "2026-02-24T18:30:00",
 *   "data": { },
 *   "error": null
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private LocalDateTime timestamp;
    private T data;
    private ErrorDetails error;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .timestamp(LocalDateTime.now())
                .data(data)
                .error(null)
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .timestamp(LocalDateTime.now())
                .data(null)
                .error(ErrorDetails.builder()
                        .code(code)
                        .message(message)
                        .build())
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ErrorDetails {
        private String code;
        private String message;
    }
}
