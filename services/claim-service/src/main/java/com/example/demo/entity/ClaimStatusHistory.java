package com.example.demo.entity;


import jakarta.persistence.*;

import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "claim_status_history")
@Data
public class ClaimStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long claimId;

    @Enumerated(EnumType.STRING)
    private ClaimStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private ClaimStatus newStatus;

    private LocalDateTime transitionedAt;
}