package com.claimswift.payment.exception;

/**
 * Invalid Claim Status Exception
 * 
 * Thrown when claim status does not allow the requested operation
 */
public class InvalidClaimStatusException extends PaymentException {

    public InvalidClaimStatusException(String message) {
        super("INVALID_CLAIM_STATUS", message);
    }
}
