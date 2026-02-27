package com.claimswift.document.repository;

import com.claimswift.document.entity.DocumentAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentAuditRepository extends JpaRepository<DocumentAudit, Long> {
}