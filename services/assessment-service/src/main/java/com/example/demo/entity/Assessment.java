package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "assessments")
@Data
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assessmentId;

    private Long claimId;

    private Long adjusterId;

    private Double assessedAmount;

    private Double deductible;

    private Double finalAmount;

    @Enumerated(EnumType.STRING)
    private AssessmentStatus status;

    private LocalDateTime assessedAt;
}