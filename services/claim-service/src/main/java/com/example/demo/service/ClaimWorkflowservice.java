package com.example.demo.service;  // ✅ lowercase 'service'

import com.example.demo.dto.ClaimDto;
import com.example.demo.entity.Claim;
import com.example.demo.entity.ClaimStatus;
import com.example.demo.entity.ClaimStatusHistory;
import com.example.demo.exception.ClaimNotFoundException;
import com.example.demo.repository.ClaimRepository;
import com.example.demo.repository.ClaimStatusHistoryRepository;
import com.example.demo.workflow.ClaimWorkflowValidator;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Service
@RequiredArgsConstructor  // ✅ Auto-injects repositories
@Transactional  // ✅ Manages DB transactions
public class ClaimWorkflowservice {

    // ✅ ONLY inject repositories - NO Application class!
    private final ClaimRepository claimRepo;
    private final ClaimStatusHistoryRepository historyRepo;

    // ✅ DELETE THIS constructor - @RequiredArgsConstructor handles it
    // ClaimWorkflowService(ClaimServiceApplication claimServiceApplication) { ... }

    public Claim submitClaim(ClaimDto dto) {
        Claim claim = new Claim();
        claim.setPolicyNumber(dto.getPolicyNumber());
        claim.setAmount(dto.getAmount());
        claim.setDescription(dto.getDescription());
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setCreatedAt(LocalDateTime.now());
        
        return claimRepo.save(claim);
    }

    public Claim updateStatus(Long claimId, ClaimStatus newStatus) {
        Claim claim = claimRepo.findById(claimId)
            .orElseThrow(() -> new RuntimeException("Claim not found: " + claimId));

        // ✅ Use your validator
        if (!ClaimWorkflowValidator.isValidTransition(claim.getStatus(), newStatus)) {
            throw new IllegalStateException("Invalid transition: " + 
                claim.getStatus() + " → " + newStatus);
        }

        ClaimStatus oldStatus = claim.getStatus();
        claim.setStatus(newStatus);
        claim.setUpdatedAt(LocalDateTime.now());
        
        Claim savedClaim = claimRepo.save(claim);

        // ✅ Audit trail
        ClaimStatusHistory history = new ClaimStatusHistory();
        history.setClaimId(claimId);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setTransitionedAt(LocalDateTime.now());
        historyRepo.save(history);

        return savedClaim;
    }

    public List<Claim> getClaimsByUser(String userId) {
        return claimRepo.findByPolicyNumberContaining(userId);
    }

    public ClaimStatus getStatus(Long id) {
        return claimRepo.findById(id)
            .orElseThrow(() -> new ClaimNotFoundException(id))
            .getStatus();
    }

    public List<ClaimStatusHistory> getHistory(Long id) {
        return historyRepo.findByClaimId(id);
    }

    public List<Claim> getClaimsByStatus(ClaimStatus status) {
        return claimRepo.findByStatus(status);
    }
}
