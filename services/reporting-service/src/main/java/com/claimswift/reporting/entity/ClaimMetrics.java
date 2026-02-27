package com.claimswift.reporting.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "claim_metrics")
public class ClaimMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== CLAIM METRICS =====
    private Long totalClaims;
    private Long approvedClaims;
    private Long rejectedClaims;
    private Double averageSettlementTime;

    // ===== PAYMENT METRICS =====
    private Long totalPayments;
    private Double totalAmountSettled;
    private Double avgSettlementProcessingTime;

    private LocalDateTime calculatedAt;

    @PrePersist
    public void onCreate() {
        calculatedAt = LocalDateTime.now();
    }
}