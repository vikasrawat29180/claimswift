package com.claimswift.reporting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.claimswift.reporting.entity.AuditReport;

public interface AuditReportRepository 
        extends JpaRepository<AuditReport, Long> {
}