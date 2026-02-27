package com.claimswift.document.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.claimswift.document.enums.DocumentStatus;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long claimId;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private Long uploadedBy;

    private LocalDateTime uploadedAt;

//    private String status;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
        this.status = DocumentStatus.ACTIVE;
    }
}