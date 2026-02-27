package com.claimswift.document.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long documentId;

    private String action;

    @Column(nullable = false)
    private Long performedBy;
    private LocalDateTime actionTime;

    @PrePersist
    public void prePersist() {
        this.actionTime = LocalDateTime.now();
    }
}