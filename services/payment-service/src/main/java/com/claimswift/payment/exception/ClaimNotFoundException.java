package com.claimswift.payment.exception;

/**
 * Claim Not Found Exception
 * 
 * Thrown when a claim is not found in the system
 */
public class ClaimNotFoundException extends PaymentException {

    public ClaimNotFoundException(String message) {
        super("CLAIM_NOT_FOUND", message);
    }
}
