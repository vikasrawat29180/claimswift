package com.claimswift.payment.entity;

/**
 * Transaction Status Enumeration
 * 
 * Status for individual transaction records:
 * - PENDING: Transaction initiated, awaiting processing
 * - COMPLETED: Transaction completed successfully
 * - FAILED: Transaction failed
 */
public enum TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED
}
