package com.claimswift.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED) // 423
public class AccountLockedException extends RuntimeException {

    public AccountLockedException() {
        super("Account is locked due to multiple failed login attempts");
    }

    public AccountLockedException(String message) {
        super(message);
    }
}