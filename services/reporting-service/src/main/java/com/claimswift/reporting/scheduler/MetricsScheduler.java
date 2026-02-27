package com.claimswift.reporting.scheduler;

import com.claimswift.reporting.service.MetricsAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetricsScheduler {

    private final MetricsAggregationService service;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void runScheduler() {
        service.aggregateMetrics();
    }
}