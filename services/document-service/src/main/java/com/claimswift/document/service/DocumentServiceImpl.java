package com.claimswift.document.service;

import com.claimswift.document.client.ClaimClient;
import com.claimswift.document.dto.ClaimValidationResponse;
import com.claimswift.document.dto.DocumentResponseDTO;
import com.claimswift.document.entity.Document;
import com.claimswift.document.entity.DocumentAudit;
import com.claimswift.document.enums.DocumentStatus;
import com.claimswift.document.exception.ResourceNotFoundException;
import com.claimswift.document.repository.DocumentAuditRepository;
import com.claimswift.document.repository.DocumentRepository;
import com.claimswift.document.util.FileValidationUtil;
import com.claimswift.document.util.SecurityUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentAuditRepository auditRepository;
    private final FileStorageService fileStorageService;
    private final ClaimClient claimClient;

    // ===============================
    // SOFT DELETE
    // ===============================
    @Override
    public void softDelete(Long documentId, Long performedBy) {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        if (document.getStatus() == DocumentStatus.INACTIVE) {
            throw new IllegalStateException("Document already inactive");
        }

        document.setStatus(DocumentStatus.INACTIVE);        //=============
//        document.setUploadedAt(LocalDateTime.now());
//======================
        documentRepository.save(document);

        auditRepository.save(
                DocumentAudit.builder()
                        .documentId(documentId)
                        .action("SOFT_DELETE")
                        .performedBy(performedBy)
                        .build()
        );
    }

    // ===============================
    // UPLOAD DOCUMENTS
    // ===============================
    @Override
    public List<DocumentResponseDTO> uploadDocuments(
            Long claimId,
            List<MultipartFile> files) {

        if (claimId == null) {
            throw new IllegalArgumentException("Claim ID is required");
        }

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("At least one file must be uploaded");
        }

        ClaimValidationResponse response = claimClient.validateClaim(claimId);

        if (!response.isValid()) {
            throw new IllegalStateException("Claim does not exist");
        }

        if ("REJECTED".equals(response.getStatus()) ||
            "PAID".equals(response.getStatus())) {

            throw new IllegalStateException(
                "Cannot upload document for this claim status");
        }

        Long uploadedBy = SecurityUtil.getCurrentUserId();

        List<DocumentResponseDTO> responseList = new ArrayList<>();

        for (MultipartFile file : files) {

            FileValidationUtil.validate(file);

            try {
                String filePath = fileStorageService.storeFile(file);

                Document document = Document.builder()
                        .claimId(claimId)
                        .fileName(file.getOriginalFilename())
                        .fileType(file.getContentType())
                        .filePath(filePath)
                        .fileSize(file.getSize())
                        .uploadedBy(uploadedBy)
                        .uploadedAt(LocalDateTime.now())
                        .status(DocumentStatus.ACTIVE)
                        .build();

                Document saved = documentRepository.save(document);

                auditRepository.save(
                        DocumentAudit.builder()
                                .documentId(saved.getId())
                                .action("UPLOAD")
                                .performedBy(uploadedBy)
                                .actionTime(LocalDateTime.now())
                                .build()
                );

                responseList.add(mapToDTO(saved));

            } catch (IOException e) {
                throw new RuntimeException("File storage failed");
            }
        }

        return responseList;
    }
    // ===============================
    // GET DOCUMENTS BY CLAIM
    // ===============================
    @Override
    public List<DocumentResponseDTO> getDocumentsByClaim(Long claimId) {

        List<Document> documents = documentRepository.findByClaimId(claimId);

        return documents.stream()
        		.filter(doc -> doc.getStatus() == DocumentStatus.ACTIVE)
                .map(this::mapToDTO)
                .toList();
    }

    // ===============================
    // GET SINGLE DOCUMENT
    // ===============================
    @Override
    public Document getDocument(Long documentId) {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        if (document.getStatus() == DocumentStatus.INACTIVE) {
            throw new IllegalStateException("Document is inactive");
        }

        return document;
    }

    // ===============================
    // MAPPER METHOD
    // ===============================
    private DocumentResponseDTO mapToDTO(Document document) {

        return DocumentResponseDTO.builder()
                .id(document.getId())
                .claimId(document.getClaimId())
                .fileName(document.getFileName())
                .fileType(document.getFileType())
                .fileSize(document.getFileSize())
                .uploadedBy(document.getUploadedBy())
                .uploadedAt(document.getUploadedAt())
                .status(document.getStatus())
                .build();
    }
}