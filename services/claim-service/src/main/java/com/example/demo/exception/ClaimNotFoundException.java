package com.example.demo.exception;


public class ClaimNotFoundException extends RuntimeException {
    
    public ClaimNotFoundException(Long claimId) {
        super("Claim not found with ID: " + claimId);
    }
    
    public ClaimNotFoundException(String message) {
        super(message);
    }
}