package com.claimswift.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
public class InvalidOtpException extends RuntimeException {

    public InvalidOtpException() {
        super("Invalid OTP code");
    }

    public InvalidOtpException(String message) {
        super(message);
    }
}