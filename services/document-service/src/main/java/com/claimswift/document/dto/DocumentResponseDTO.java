package com.claimswift.document.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import com.claimswift.document.enums.DocumentStatus;

@Data
@Builder
public class DocumentResponseDTO {

    private Long id;
    private Long claimId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private Long uploadedBy;
    private LocalDateTime uploadedAt;
    private DocumentStatus status;
    }