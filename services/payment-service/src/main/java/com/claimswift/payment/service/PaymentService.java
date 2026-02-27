package com.claimswift.payment.service;

import com.claimswift.payment.dto.PaymentRequest;
import com.claimswift.payment.dto.PaymentResponse;
import java.util.List;

/**
 * Payment Service Interface
 * 
 * Defines the contract for payment processing operations
 */
public interface PaymentService {

    /**
     * Process payment for an approved claim
     * 
     * @param request - Payment request containing claim and amount details
     * @return Payment response with payment details
     */
    PaymentResponse processPayment(PaymentRequest request);

    /**
     * Get payment by ID
     * 
     * @param paymentId - The payment ID
     * @return Payment response
     */
    PaymentResponse getPaymentById(Long paymentId);

    /**
     * Get payment by claim ID
     * 
     * @param claimId - The claim ID
     * @return Payment response
     */
    PaymentResponse getPaymentByClaimId(Long claimId);

    /**
     * Get all payments
     * 
     * @return List of payment responses
     */
    List<PaymentResponse> getAllPayments();

    /**
     * Retry failed payment
     * 
     * @param paymentId - The payment ID to retry
     * @return Payment response
     */
    PaymentResponse retryPayment(Long paymentId);
}
