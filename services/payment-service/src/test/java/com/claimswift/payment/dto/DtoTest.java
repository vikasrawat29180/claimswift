package com.claimswift.payment.dto;

import com.claimswift.payment.entity.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DTO Unit Tests
 * 
 * Tests for Data Transfer Objects
 */
class DtoTest {

    // =============================================
    // TEST: ApiResponse - Success Response
    // =============================================
    @Test
    @DisplayName("ApiResponse - Success Response")
    void apiResponse_Success() {
        ApiResponse<String> response = ApiResponse.success("Test Data");

        assertTrue(response.isSuccess());
        assertNotNull(response.getTimestamp());
        assertEquals("Test Data", response.getData());
        assertNull(response.getError());
    }

    // =============================================
    // TEST: ApiResponse - Error Response
    // =============================================
    @Test
    @DisplayName("ApiResponse - Error Response")
    void apiResponse_Error() {
        ApiResponse<Void> response = ApiResponse.error("ERROR_CODE", "Error message");

        assertFalse(response.isSuccess());
        assertNotNull(response.getTimestamp());
        assertNull(response.getData());
        assertNotNull(response.getError());
        assertEquals("ERROR_CODE", response.getError().getCode());
        assertEquals("Error message", response.getError().getMessage());
    }

    // =============================================
    // TEST: PaymentRequest - Builder and Validation
    // =============================================
    @Test
    @DisplayName("PaymentRequest - Builder and Getters")
    void paymentRequest_Builder() {
        PaymentRequest request = PaymentRequest.builder()
                .claimId(1L)
                .approvedAmount(new BigDecimal("50000.00"))
                .bankAccountNumber("1234567890")
                .bankName("HDFC Bank")
                .ifscCode("HDFC0001234")
                .accountHolderName("John Doe")
                .build();

        assertEquals(1L, request.getClaimId());
        assertEquals(new BigDecimal("50000.00"), request.getApprovedAmount());
        assertEquals("1234567890", request.getBankAccountNumber());
        assertEquals("HDFC Bank", request.getBankName());
        assertEquals("HDFC0001234", request.getIfscCode());
        assertEquals("John Doe", request.getAccountHolderName());
    }

    // =============================================
    // TEST: PaymentResponse - Builder and Getters
    // =============================================
    @Test
    @DisplayName("PaymentResponse - Builder and Getters")
    void paymentResponse_Builder() {
        LocalDateTime now = LocalDateTime.now();
        
        PaymentResponse response = PaymentResponse.builder()
                .paymentId(1L)
                .claimId(100L)
                .approvedAmount(new BigDecimal("50000.00"))
                .paymentReference("PAY-ABC123")
                .status(PaymentStatus.SUCCESS)
                .processedAt(now)
                .createdAt(now)
                .build();

        assertEquals(1L, response.getPaymentId());
        assertEquals(100L, response.getClaimId());
        assertEquals(new BigDecimal("50000.00"), response.getApprovedAmount());
        assertEquals("PAY-ABC123", response.getPaymentReference());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
    }

    // =============================================
    // TEST: ClaimResponse - Builder and Getters
    // =============================================
    @Test
    @DisplayName("ClaimResponse - Builder and Getters")
    void claimResponse_Builder() {
        ClaimResponse response = ClaimResponse.builder()
                .claimId(1L)
                .claimNumber("CLM-2026-001")
                .policyholderId(5L)
                .status("APPROVED")
                .approvedAmount(new BigDecimal("50000.00"))
                .build();

        assertEquals(1L, response.getClaimId());
        assertEquals("CLM-2026-001", response.getClaimNumber());
        assertEquals(5L, response.getPolicyholderId());
        assertEquals("APPROVED", response.getStatus());
        assertEquals(new BigDecimal("50000.00"), response.getApprovedAmount());
    }

    // =============================================
    // TEST: ClaimStatusUpdateRequest - Builder
    // =============================================
    @Test
    @DisplayName("ClaimStatusUpdateRequest - Builder")
    void claimStatusUpdateRequest_Builder() {
        ClaimStatusUpdateRequest request = ClaimStatusUpdateRequest.builder()
                .newStatus("PAID")
                .build();

        assertEquals("PAID", request.getNewStatus());
    }

    // =============================================
    // TEST: NotificationRequest - Builder
    // =============================================
    @Test
    @DisplayName("NotificationRequest - Builder")
    void notificationRequest_Builder() {
        NotificationRequest request = NotificationRequest.builder()
                .userId(5L)
                .claimId(12L)
                .type("PAYMENT_SUCCESS")
                .message("Your payment has been processed")
                .build();

        assertEquals(5L, request.getUserId());
        assertEquals(12L, request.getClaimId());
        assertEquals("PAYMENT_SUCCESS", request.getType());
        assertEquals("Your payment has been processed", request.getMessage());
    }

    // =============================================
    // TEST: PaymentResponse TransactionResponse
    // =============================================
    @Test
    @DisplayName("PaymentResponse TransactionResponse - Builder")
    void paymentResponseTransaction_Builder() {
        PaymentResponse.TransactionResponse transaction = 
                PaymentResponse.TransactionResponse.builder()
                        .transactionId(1L)
                        .bankReference("TXN-123456")
                        .transactionStatus("COMPLETED")
                        .build();

        assertEquals(1L, transaction.getTransactionId());
        assertEquals("TXN-123456", transaction.getBankReference());
        assertEquals("COMPLETED", transaction.getTransactionStatus());
    }
}
