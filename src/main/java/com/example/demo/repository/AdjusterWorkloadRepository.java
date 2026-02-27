package com.example.demo.repository;

import com.example.demo.entity.AdjusterWorkload;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdjusterWorkloadRepository extends JpaRepository<AdjusterWorkload, Long> {
    Optional<AdjusterWorkload> findByAdjusterId(Long adjusterId);
}