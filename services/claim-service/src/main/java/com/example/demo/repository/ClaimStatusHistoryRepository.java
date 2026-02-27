package com.example.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.ClaimStatusHistory;

import java.util.List;

public interface ClaimStatusHistoryRepository
        extends JpaRepository<ClaimStatusHistory, Long> {

    List<ClaimStatusHistory> findByClaimId(Long claimId);
}
