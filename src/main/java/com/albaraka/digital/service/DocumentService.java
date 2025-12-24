package com.albaraka.digital.service;

import com.albaraka.digital.dto.response.DocumentDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    DocumentDto uploadDocument(Long operationId, MultipartFile file);

    List<DocumentDto> getDocumentsByOperationId(Long operationId);

    byte[] downloadDocument(Long documentId);
}
