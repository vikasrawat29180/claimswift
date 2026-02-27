package com.claimswift.payment.exception;

/**
 * Payment Already Exists Exception
 * 
 * Thrown when attempting to create a payment for a claim that already has a payment
 */
public class PaymentAlreadyExistsException extends PaymentException {

    public PaymentAlreadyExistsException(String message) {
        super("PAYMENT_ALREADY_EXISTS", message);
    }
}
