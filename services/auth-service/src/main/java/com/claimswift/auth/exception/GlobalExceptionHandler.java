package com.claimswift.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 → User not found
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleUserNotFound(UserNotFoundException ex) {
        Map<String,String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 401 → Invalid credentials
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String,String>> invalidCreds(InvalidCredentialsException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", ex.getMessage()));
    }

    // 423 → Account locked
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<Map<String,String>> handleAccountLocked(AccountLockedException ex) {
        Map<String,String> error = new HashMap<>();
        error.put("error", "Account is locked due to multiple failed login attempts");
        return ResponseEntity.status(HttpStatus.LOCKED).body(error);
    }

    // 401 → Invalid OTP
    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<Map<String,String>> handleInvalidOtp(InvalidOtpException ex) {
        Map<String,String> error = new HashMap<>();
        error.put("error", "Invalid OTP code");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // 400 → Validation errors for @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    // 400 → Constraint violations (e.g., path variables)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String,String>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String,String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    // 400 → Type mismatch (e.g., passing text instead of number)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String,String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String,String> error = new HashMap<>();
        error.put("error", "Invalid value for parameter '" + ex.getName() + "'");
        return ResponseEntity.badRequest().body(error);
    }

    // Fallback for any other unhandled exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> handleOtherExceptions(Exception ex) {
        Map<String,String> error = new HashMap<>();
        error.put("error", "Internal server error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}