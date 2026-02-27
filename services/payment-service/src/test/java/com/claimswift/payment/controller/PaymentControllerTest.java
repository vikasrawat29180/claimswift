package com.claimswift.payment.controller;

import com.claimswift.payment.dto.*;
import com.claimswift.payment.exception.*;
import com.claimswift.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Payment Controller Unit Tests
 * 
 * Tests for PaymentController using MockMvc
 */
@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    private PaymentRequest validPaymentRequest;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        // Setup valid payment request
        validPaymentRequest = PaymentRequest.builder()
                .claimId(1L)
                .approvedAmount(new BigDecimal("50000.00"))
                .bankAccountNumber("1234567890")
                .bankName("HDFC Bank")
                .ifscCode("HDFC0001234")
                .accountHolderName("John Doe")
                .build();

        // Setup payment response
        paymentResponse = PaymentResponse.builder()
                .paymentId(1L)
                .claimId(1L)
                .approvedAmount(new BigDecimal("50000.00"))
                .paymentReference("PAY-ABC12345")
                .status(com.claimswift.payment.entity.PaymentStatus.INITIATED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // =============================================
    // TEST: Process Payment - Success
    // =============================================
    @Test
    @DisplayName("Process Payment - Success")
    void processPayment_Success() throws Exception {
        // Arrange
        when(paymentService.processPayment(any(PaymentRequest.class)))
                .thenReturn(paymentResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPaymentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.paymentId").value(1))
                .andExpect(jsonPath("$.data.claimId").value(1))
                .andExpect(jsonPath("$.data.status").value("INITIATED"));
    }

    // =============================================
    // TEST: Process Payment - Validation Error
    // =============================================
    @Test
    @DisplayName("Process Payment - Validation Error")
    void processPayment_ValidationError() throws Exception {
        // Arrange - Invalid request (missing required fields)
        PaymentRequest invalidRequest = PaymentRequest.builder()
                .claimId(null)  // Invalid - null claimId
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // =============================================
    // TEST: Get Payment By ID - Success
    // =============================================
    @Test
    @DisplayName("Get Payment By ID - Success")
    void getPaymentById_Success() throws Exception {
        // Arrange
        when(paymentService.getPaymentById(1L)).thenReturn(paymentResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.paymentId").value(1));
    }

    // =============================================
    // TEST: Get Payment By ID - Not Found
    // =============================================
    @Test
    @DisplayName("Get Payment By ID - Not Found")
    void getPaymentById_NotFound() throws Exception {
        // Arrange
        when(paymentService.getPaymentById(999L))
                .thenThrow(new PaymentNotFoundException("Payment not found with ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/payments/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PAYMENT_NOT_FOUND"));
    }

    // =============================================
    // TEST: Get Payment By Claim ID - Success
    // =============================================
    @Test
    @DisplayName("Get Payment By Claim ID - Success")
    void getPaymentByClaimId_Success() throws Exception {
        // Arrange
        when(paymentService.getPaymentByClaimId(1L)).thenReturn(paymentResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/payments/claim/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.claimId").value(1));
    }

    // =============================================
    // TEST: Get All Payments - Success
    // =============================================
    @Test
    @DisplayName("Get All Payments - Success")
    void getAllPayments_Success() throws Exception {
        // Arrange
        when(paymentService.getAllPayments()).thenReturn(List.of(paymentResponse));

        // Act & Assert
        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    // =============================================
    // TEST: Retry Payment - Success
    // =============================================
    @Test
    @DisplayName("Retry Payment - Success")
    void retryPayment_Success() throws Exception {
        // Arrange
        paymentResponse.setStatus(com.claimswift.payment.entity.PaymentStatus.SUCCESS);
        when(paymentService.retryPayment(1L)).thenReturn(paymentResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/payments/1/retry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("SUCCESS"));
    }

    // =============================================
    // TEST: Retry Payment - Invalid State
    // =============================================
    @Test
    @DisplayName("Retry Payment - Invalid State")
    void retryPayment_InvalidState() throws Exception {
        // Arrange
        when(paymentService.retryPayment(1L))
                .thenThrow(new InvalidPaymentStateException("Only failed payments can be retried"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/payments/1/retry"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_PAYMENT_STATE"));
    }

    // =============================================
    // TEST: Health Check - Success
    // =============================================
    @Test
    @DisplayName("Health Check - Success")
    void healthCheck_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/payments/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Payment Service is running"));
    }

    // =============================================
    // TEST: Process Payment - Claim Not Approved
    // =============================================
    @Test
    @DisplayName("Process Payment - Claim Not Approved")
    void processPayment_ClaimNotApproved() throws Exception {
        // Arrange
        when(paymentService.processPayment(any(PaymentRequest.class)))
                .thenThrow(new InvalidClaimStatusException("Claim status must be APPROVED"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPaymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_CLAIM_STATUS"));
    }

    // =============================================
    // TEST: Process Payment - Payment Already Exists
    // =============================================
    @Test
    @DisplayName("Process Payment - Payment Already Exists")
    void processPayment_PaymentAlreadyExists() throws Exception {
        // Arrange
        when(paymentService.processPayment(any(PaymentRequest.class)))
                .thenThrow(new PaymentAlreadyExistsException("Payment already exists for claim"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPaymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PAYMENT_ALREADY_EXISTS"));
    }
}
