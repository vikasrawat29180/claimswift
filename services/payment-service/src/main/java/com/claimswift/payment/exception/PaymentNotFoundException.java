package com.claimswift.payment.exception;

/**
 * Payment Not Found Exception
 * 
 * Thrown when a payment is not found in the system
 */
public class PaymentNotFoundException extends PaymentException {

    public PaymentNotFoundException(String message) {
        super("PAYMENT_NOT_FOUND", message);
    }
}
