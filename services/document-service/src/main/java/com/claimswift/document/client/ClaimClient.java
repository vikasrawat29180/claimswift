package com.claimswift.document.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.claimswift.document.dto.ClaimValidationResponse;

@FeignClient(name = "claim-service", url = "${claim-service.url}")

public interface ClaimClient {

    @GetMapping("/api/v1/claims/{id}/validate")
    ClaimValidationResponse validateClaim(@PathVariable Long id);
}