package com.claimswift.reporting.service;
import com.claimswift.reporting.repository.AuditReportRepository;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.claimswift.reporting.entity.AuditReport;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class AuditService {

    private final AuditReportRepository repository;

    public void saveAudit(String user, String type) {

        AuditReport audit = new AuditReport();
        audit.setGeneratedBy(user);
        audit.setReportType(type);
        audit.setGeneratedAt(LocalDateTime.now());

        repository.save(audit);
    }
}