package com.example.demo.repository;

import com.example.demo.entity.AssessmentAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentAuditRepository extends JpaRepository<AssessmentAudit, Long> {
}