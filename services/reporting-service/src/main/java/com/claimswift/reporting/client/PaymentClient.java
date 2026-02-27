package com.claimswift.reporting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.claimswift.reporting.dto.PaymentSummaryDTO;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @GetMapping("/internal/payments/summary")
    PaymentSummaryDTO getPaymentSummary();
}