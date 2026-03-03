package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "assessment_audit")
@Data
public class AssessmentAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long claimId;
    private String action;
    private String oldStatus;
    private String newStatus;
    private Long performedBy;
    private LocalDateTime timestamp;
}