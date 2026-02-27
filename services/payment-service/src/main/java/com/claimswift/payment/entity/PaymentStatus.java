package com.claimswift.payment.entity;

/**
 * Payment Status Enumeration
 * 
 * Global status definitions for payment processing:
 * - INITIATED: Payment has been initiated
 * - SUCCESS: Payment processed successfully
 * - FAILED: Payment processing failed
 */
public enum PaymentStatus {
    INITIATED,
    SUCCESS,
    FAILED
}
