package com.claimswift.payment.exception;

/**
 * Invalid Payment State Exception
 * 
 * Thrown when an operation is attempted on a payment in an invalid state
 */
public class InvalidPaymentStateException extends PaymentException {

    public InvalidPaymentStateException(String message) {
        super("INVALID_PAYMENT_STATE", message);
    }
}
