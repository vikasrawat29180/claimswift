package com.claimswift.document.service;

import com.claimswift.document.dto.DocumentResponseDTO;
import com.claimswift.document.entity.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    List<DocumentResponseDTO> uploadDocuments(Long claimId,
                                   List<MultipartFile> files
                                   );

    List<DocumentResponseDTO> getDocumentsByClaim(Long claimId);

    Document getDocument(Long documentId);

    void softDelete(Long documentId, Long performedBy);
    
    }