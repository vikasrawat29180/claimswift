package com.example.demo.entity;

import java.time.LocalDateTime;



import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String policyNumber;

    private Double amount;

    private String description;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}