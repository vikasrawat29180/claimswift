package com.example.demo.client;

import com.example.demo.dto.ClaimResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ClaimClient {

    private final WebClient webClient;

    @Value("${claim.base-url}")
    private String claimBaseUrl;

    // ✅ Validate claim exists and is UNDER_REVIEW
    public ClaimResponseDTO validateClaimUnderReview(Long claimId) {

        ClaimResponseDTO claim = webClient.get()
                .uri(claimBaseUrl + "/claims/" + claimId)
                .retrieve()
                .bodyToMono(ClaimResponseDTO.class)
                .block();

        if (claim == null) {
            throw new RuntimeException("Claim not found: " + claimId);
        }

        if (!"UNDER_REVIEW".equalsIgnoreCase(claim.getStatus())) {
            throw new RuntimeException(
                    "Claim not in UNDER_REVIEW state. Current: " + claim.getStatus()
            );
        }

        return claim;
    }

    // ✅ Update claim status after approval/rejection
    public void updateClaimStatus(Long claimId, String newStatus) {

        webClient.put()
                .uri(claimBaseUrl + "/claims/" + claimId + "/status")
                .bodyValue(new StatusUpdate(newStatus))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public record StatusUpdate(String newStatus) {}
}