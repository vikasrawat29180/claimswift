package com.claimswift.payment.controller;

import com.claimswift.payment.dto.*;
import com.claimswift.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Payment Controller
 * 
 * REST API endpoints for payment processing operations
 * 
 * API Endpoints:
 * - POST /api/v1/payments - Process a new payment
 * - GET /api/v1/payments/{paymentId} - Get payment by ID
 * - GET /api/v1/payments/claim/{claimId} - Get payment by claim ID
 * - GET /api/v1/payments - Get all payments
 * - POST /api/v1/payments/{paymentId}/retry - Retry a failed payment
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Process a new payment
     * 
     * Integration Rule:
     * - Verifies claim status = APPROVED via Claim Service API
     * - Processes payment
     * - Updates claim status to PAID after success
     * 
     * @param request - Payment request with claim and amount details
     * @return Payment response with payment details
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody PaymentRequest request) {
        
        log.info("Received payment request for claim ID: {}", request.getClaimId());
        
        PaymentResponse response = paymentService.processPayment(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    /**
     * Get payment by ID
     * 
     * @param paymentId - The payment ID
     * @return Payment response
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable Long paymentId) {
        
        log.info("Fetching payment with ID: {}", paymentId);
        
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        
        return ResponseEntity
                .ok(ApiResponse.success(response));
    }

    /**
     * Get payment by claim ID
     * 
     * @param claimId - The claim ID
     * @return Payment response
     */
    @GetMapping("/claim/{claimId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByClaimId(
            @PathVariable Long claimId) {
        
        log.info("Fetching payment for claim ID: {}", claimId);
        
        PaymentResponse response = paymentService.getPaymentByClaimId(claimId);
        
        return ResponseEntity
                .ok(ApiResponse.success(response));
    }

    /**
     * Get all payments
     * 
     * @return List of payment responses
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {
        
        log.info("Fetching all payments");
        
        List<PaymentResponse> payments = paymentService.getAllPayments();
        
        return ResponseEntity
                .ok(ApiResponse.success(payments));
    }

    /**
     * Retry a failed payment
     * 
     * @param paymentId - The payment ID to retry
     * @return Payment response
     */
    @PostMapping("/{paymentId}/retry")
    public ResponseEntity<ApiResponse<PaymentResponse>> retryPayment(
            @PathVariable Long paymentId) {
        
        log.info("Retrying payment with ID: {}", paymentId);
        
        PaymentResponse response = paymentService.retryPayment(paymentId);
        
        return ResponseEntity
                .ok(ApiResponse.success(response));
    }

    /**
     * Health check endpoint
     * 
     * @return Success message
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity
                .ok(ApiResponse.success("Payment Service is running"));
    }
}
