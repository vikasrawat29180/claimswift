package com.claimswift.payment.dto;

import lombok.*;

/**
 * Claim Status Update Request DTO
 * 
 * Request body for updating claim status via Claim Service API
 * Used to change claim status to PAID after successful payment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimStatusUpdateRequest {

    private String newStatus;
}
