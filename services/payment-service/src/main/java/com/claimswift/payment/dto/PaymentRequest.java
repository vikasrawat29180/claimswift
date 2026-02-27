package com.claimswift.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;

/**
 * Payment Request DTO
 * 
 * Request body for initiating a payment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    @NotNull(message = "Claim ID is required")
    private Long claimId;

    @NotNull(message = "Approved amount is required")
    @Positive(message = "Approved amount must be positive")
    private BigDecimal approvedAmount;

    private String bankAccountNumber;
    private String bankName;
    private String ifscCode;
    private String accountHolderName;
}
