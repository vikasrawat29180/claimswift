package com.claimswift.payment.dto;

import com.claimswift.payment.entity.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Response DTO
 * 
 * Response body for payment operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long paymentId;
    private Long claimId;
    private BigDecimal approvedAmount;
    private String paymentReference;
    private PaymentStatus status;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    private TransactionResponse transaction;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransactionResponse {
        private Long transactionId;
        private String bankReference;
        private String transactionStatus;
        private LocalDateTime transactionTime;
        private String failureReason;
    }
}
