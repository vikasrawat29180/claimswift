package com.claimswift.reporting.repository;

import com.claimswift.reporting.entity.ClaimMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimMetricsRepository
        extends JpaRepository<ClaimMetrics, Long> {

    ClaimMetrics findTopByOrderByCalculatedAtDesc();
}