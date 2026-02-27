package com.claimswift.payment.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Claim Response DTO
 * 
 * DTO for receiving claim information from Claim Service
 * This is used for inter-service communication via Feign Client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimResponse {

    private Long claimId;
    private String claimNumber;
    private Long policyholderId;
    private String claimType;
    private String status;
    private BigDecimal estimatedAmount;
    private BigDecimal approvedAmount;
    private LocalDateTime incidentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
