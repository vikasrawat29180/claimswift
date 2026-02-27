package com.claimswift.reporting.service;

import com.claimswift.reporting.client.ClaimClient;
import com.claimswift.reporting.client.PaymentClient;
import com.claimswift.reporting.dto.ClaimSummaryDTO;
import com.claimswift.reporting.dto.PaymentSummaryDTO;
import com.claimswift.reporting.entity.ClaimMetrics;
import com.claimswift.reporting.repository.ClaimMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetricsAggregationService {

    private final ClaimClient claimClient;
    private final PaymentClient paymentClient;
    private final ClaimMetricsRepository repository;

    public void aggregateMetrics() {

        try {

            ClaimSummaryDTO claim = claimClient.getClaimSummary();
            PaymentSummaryDTO payment = paymentClient.getPaymentSummary();

            ClaimMetrics metrics = new ClaimMetrics();

            // Claim Stats
            metrics.setTotalClaims(claim.getTotal());
            metrics.setApprovedClaims(claim.getApproved());
            metrics.setRejectedClaims(claim.getRejected());
            metrics.setAverageSettlementTime(claim.getAvgSettlementTime());

            // Payment Stats
            metrics.setTotalPayments(payment.getTotalPayments());
            metrics.setTotalAmountSettled(payment.getTotalAmountSettled());
            metrics.setAvgSettlementProcessingTime(
                    payment.getAvgSettlementProcessingTime()
            );

            repository.save(metrics);

        } catch (Exception e) {
            System.out.println("Aggregation failed: " + e.getMessage());
        }
    }
}