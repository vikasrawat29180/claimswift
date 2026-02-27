package com.claimswift.document.controller;

import com.claimswift.document.dto.ApiResponse;
import com.claimswift.document.dto.DocumentResponseDTO;
import com.claimswift.document.entity.Document;
import com.claimswift.document.entity.DocumentAudit;
import com.claimswift.document.repository.DocumentAuditRepository;
import com.claimswift.document.service.DocumentService;
import com.claimswift.document.service.FileStorageService;
import com.claimswift.document.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final FileStorageService fileStorageService;
    private final DocumentAuditRepository auditRepository;

    // ===============================
    // UPLOAD
    // ===============================
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<List<DocumentResponseDTO>>> upload(
            @RequestParam Long claimId,
            @RequestParam List<MultipartFile> files) {

        List<DocumentResponseDTO> response =
                documentService.uploadDocuments(claimId, files);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Documents uploaded successfully")
        );
    }

    // ===============================
    // GET DOCUMENTS BY CLAIM
    // ===============================
    @GetMapping("/claim/{claimId}")
    public ResponseEntity<ApiResponse<List<DocumentResponseDTO>>> getByClaim(
            @PathVariable Long claimId) {

        List<DocumentResponseDTO> documents =
                documentService.getDocumentsByClaim(claimId);

        return ResponseEntity.ok(
                ApiResponse.success(documents, "Documents fetched successfully")
        );
    }

    // ===============================
    // DOWNLOAD
    // ===============================
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws Exception {

        Document document = documentService.getDocument(id);

        Path path = fileStorageService.loadFile(document.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        Long userId = SecurityUtil.getCurrentUserId();

        // Save audit
        auditRepository.save(
                DocumentAudit.builder()
                        .documentId(id)
                        .action("DOWNLOAD")
                        .performedBy(userId)
                        .actionTime(LocalDateTime.now())
                        .build()
        );

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getFileName() + "\"")
                .body(resource);
    }

    // ===============================
    // SOFT DELETE
    // ===============================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> softDelete(@PathVariable Long id) {

        Long userId = SecurityUtil.getCurrentUserId();

        documentService.softDelete(id, userId);

        return ResponseEntity.ok(
                ApiResponse.success("INACTIVE")
        );
    }
}