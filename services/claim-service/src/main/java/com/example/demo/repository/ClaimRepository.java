package com.example.demo.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Claim;
import com.example.demo.entity.ClaimStatus;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByPolicyNumberContaining(String policyNumber);
    List<Claim> findByStatus(ClaimStatus status);

}

