package com.claimswift.document;

import com.claimswift.document.entity.Document;
import com.claimswift.document.repository.DocumentRepository;
import com.claimswift.document.service.DocumentServiceImpl;
import com.claimswift.document.service.FileStorageService;
import com.claimswift.document.repository.DocumentAuditRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentAuditRepository auditRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private DocumentServiceImpl documentService;

    public DocumentServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDocument_NotFound() {
        when(documentRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class,
                () -> documentService.getDocument(1L));
    }
}