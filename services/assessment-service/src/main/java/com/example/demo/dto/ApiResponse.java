package com.example.demo.dto;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        boolean success,
        LocalDateTime timestamp,
        T data,
        Object error
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, LocalDateTime.now(), data, null);
    }

    public static ApiResponse<?> error(Object error) {
        return new ApiResponse<>(false, LocalDateTime.now(), null, error);
    }
}