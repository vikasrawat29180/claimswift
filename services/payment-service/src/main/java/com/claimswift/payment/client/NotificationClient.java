package com.claimswift.payment.client;

import com.claimswift.payment.dto.ApiResponse;
import com.claimswift.payment.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Notification Client - Feign Client for Notification Service Communication
 * 
 * This client is used to send notifications to users after payment processing
 * 
 * Integration Rules:
 * - Trigger notification after successful payment
 * - Notify user about payment status changes
 */
@FeignClient(name = "notification-service", url = "${services.notification-service.url:http://localhost:8085}")
public interface NotificationClient {

    /**
     * Send notification to user
     * 
     * @param request - Notification request containing user details and message
     * @return API response
     */
    @PostMapping("/api/v1/notifications")
    ResponseEntity<ApiResponse<Void>> sendNotification(@RequestBody NotificationRequest request);
}
