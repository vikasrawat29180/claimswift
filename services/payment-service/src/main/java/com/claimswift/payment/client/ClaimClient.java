package com.claimswift.payment.client;

import com.claimswift.payment.dto.ApiResponse;
import com.claimswift.payment.dto.ClaimResponse;
import com.claimswift.payment.dto.ClaimStatusUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Claim Client - Feign Client for Claim Service Communication
 * 
 * This client is used to communicate with the Claim Service API
 * 
 * Integration Rules:
 * - Fetch claim details to verify status = APPROVED
 * - Update claim status to PAID after successful payment
 */
@FeignClient(name = "claim-service", url = "${services.claim-service.url:http://localhost:8081}")
public interface ClaimClient {

    /**
     * Get claim by ID
     * 
     * @param claimId - The claim ID
     * @return Claim details
     */
    @GetMapping("/api/v1/claims/{claimId}")
    ResponseEntity<ApiResponse<ClaimResponse>> getClaim(@PathVariable("claimId") Long claimId);

    /**
     * Update claim status
     * 
     * @param claimId - The claim ID
     * @param request - Status update request
     * @return Updated claim details
     */
    @PutMapping("/api/v1/claims/{claimId}/status")
    ResponseEntity<ApiResponse<ClaimResponse>> updateClaimStatus(
            @PathVariable("claimId") Long claimId,
            @RequestBody ClaimStatusUpdateRequest request);
}
