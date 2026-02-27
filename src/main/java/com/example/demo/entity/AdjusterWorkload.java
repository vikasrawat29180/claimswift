package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "adjuster_workload")
@Data
public class AdjusterWorkload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adjusterId;
    private Integer activeClaimCount;
    private LocalDateTime updatedAt;
}