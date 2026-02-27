package com.claimswift.reporting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.claimswift.reporting.dto.ClaimSummaryDTO;


@FeignClient(name = "claim-service")
public interface ClaimClient {

    @GetMapping("/internal/claims/summary")
    ClaimSummaryDTO getClaimSummary();
}