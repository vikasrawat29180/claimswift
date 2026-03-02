package com.example.demo.dto;

import com.example.demo.entity.AssessmentStatus;
import com.example.demo.entity.Decision;
import java.time.LocalDateTime;

public record AssessmentResponse(
        Long id,
        Long claimId,
        Long adjusterId,
        Decision decision,
        Double approvedAmount,
        AssessmentStatus status,
        LocalDateTime createdAt
) {}