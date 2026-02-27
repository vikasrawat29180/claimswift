package com.claimswift.payment.dto;

import lombok.*;

/**
 * Notification Request DTO
 * 
 * Request body for triggering notifications via Notification Service API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

    private Long userId;
    private Long claimId;
    private String type;
    private String message;
}
