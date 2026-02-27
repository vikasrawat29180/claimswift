package com.claimswift.payment.exception;

import lombok.Getter;

/**
 * Base Payment Exception
 * 
 * Base class for all payment-related exceptions
 */
@Getter
public class PaymentException extends RuntimeException {

    private final String errorCode;

    public PaymentException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PaymentException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
