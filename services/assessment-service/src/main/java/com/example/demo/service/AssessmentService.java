package com.example.demo.service;

import com.example.demo.client.ClaimClient;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository assessmentRepo;
    private final AssignmentRepository assignmentRepo;
    private final AdjusterWorkloadRepository workloadRepo;
    private final AssessmentAuditRepository auditRepo;
    private final ClaimClient claimClient;

    // CREATE
    public Assessment createAssessment(Long claimId) {

        claimClient.validateClaimUnderReview(claimId);

        Assessment a = new Assessment();
        a.setClaimId(claimId);
        a.setStatus(AssessmentStatus.PENDING);
        a.setAssessedAt(LocalDateTime.now());

        return assessmentRepo.save(a);
    }

    // ASSIGN
    public Assessment assignAdjuster(Long claimId, Long adjusterId, Long managerId) {

        Assessment a = assessmentRepo.findByClaimId(claimId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        a.setAdjusterId(adjusterId);
        assessmentRepo.save(a);

        Assignment assign = new Assignment();
        assign.setClaimId(claimId);
        assign.setAdjusterId(adjusterId);
        assign.setAssignedBy(managerId);
        assign.setAssignedAt(LocalDateTime.now());
        assignmentRepo.save(assign);

        AdjusterWorkload workload = workloadRepo
                .findByAdjusterId(adjusterId)
                .orElseGet(() -> {
                    AdjusterWorkload w = new AdjusterWorkload();
                    w.setAdjusterId(adjusterId);
                    w.setActiveClaimCount(0);
                    return w;
                });

        workload.setActiveClaimCount(workload.getActiveClaimCount() + 1);
        workload.setUpdatedAt(LocalDateTime.now());
        workloadRepo.save(workload);

        return a;
    }

    // APPROVE
    public Assessment approve(Long claimId, Double assessedAmount, Long userId) {

        Assessment a = assessmentRepo.findByClaimId(claimId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        double deductible = assessedAmount * 0.10;
        double finalAmount = assessedAmount - deductible;

        AssessmentStatus oldStatus = a.getStatus();

        a.setAssessedAmount(assessedAmount);
        a.setDeductible(deductible);
        a.setFinalAmount(finalAmount);
        a.setStatus(AssessmentStatus.APPROVED);
        a.setAssessedAt(LocalDateTime.now());

        assessmentRepo.save(a);

        claimClient.updateClaimStatus(claimId, "APPROVED");

        reduceWorkload(a.getAdjusterId());

        saveAudit(claimId, "APPROVE", oldStatus, AssessmentStatus.APPROVED, userId);

        return a;
    }

    // REJECT
    public Assessment reject(Long claimId, Long userId) {

        Assessment a = assessmentRepo.findByClaimId(claimId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        AssessmentStatus oldStatus = a.getStatus();

        a.setStatus(AssessmentStatus.REJECTED);
        a.setAssessedAt(LocalDateTime.now());

        assessmentRepo.save(a);

        claimClient.updateClaimStatus(claimId, "REJECTED");

        reduceWorkload(a.getAdjusterId());

        saveAudit(claimId, "REJECT", oldStatus, AssessmentStatus.REJECTED, userId);

        return a;
    }

    // ADJUST
    public Assessment adjust(Long claimId, Double newAmount, Long userId) {

        Assessment a = assessmentRepo.findByClaimId(claimId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        if (newAmount > a.getAssessedAmount()) {
            throw new RuntimeException("Adjusted amount cannot exceed assessed amount");
        }

        double deductible = newAmount * 0.10;
        double finalAmount = newAmount - deductible;

        AssessmentStatus oldStatus = a.getStatus();

        a.setAssessedAmount(newAmount);
        a.setDeductible(deductible);
        a.setFinalAmount(finalAmount);
        a.setAssessedAt(LocalDateTime.now());

        assessmentRepo.save(a);

        saveAudit(claimId, "ADJUST", oldStatus, oldStatus, userId);

        return a;
    }

    private void reduceWorkload(Long adjusterId) {
        if (adjusterId == null) return;

        workloadRepo.findByAdjusterId(adjusterId).ifPresent(w -> {
            w.setActiveClaimCount(Math.max(0, w.getActiveClaimCount() - 1));
            w.setUpdatedAt(LocalDateTime.now());
            workloadRepo.save(w);
        });
    }

    private void saveAudit(Long claimId, String action,
                           AssessmentStatus oldStatus,
                           AssessmentStatus newStatus,
                           Long userId) {

        AssessmentAudit audit = new AssessmentAudit();
        audit.setClaimId(claimId);
        audit.setAction(action);
        audit.setOldStatus(oldStatus.name());
        audit.setNewStatus(newStatus.name());
        audit.setPerformedBy(userId);
        audit.setTimestamp(LocalDateTime.now());

        auditRepo.save(audit);
    }
}