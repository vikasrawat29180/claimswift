package com.example.demo.repository;

import com.example.demo.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    Optional<Assessment> findByClaimId(Long claimId);
    List<Assessment> findByAdjusterId(Long adjusterId);
}