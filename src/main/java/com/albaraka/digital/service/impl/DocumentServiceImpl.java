package com.albaraka.digital.service.impl;

import com.albaraka.digital.dto.response.DocumentDto;
import com.albaraka.digital.exception.InvalidOperationException;
import com.albaraka.digital.exception.ResourceNotFoundException;
import com.albaraka.digital.mapper.OperationMapper;
import com.albaraka.digital.model.Document;
import com.albaraka.digital.model.Operation;
import com.albaraka.digital.repository.DocumentRepository;
import com.albaraka.digital.repository.OperationRepository;
import com.albaraka.digital.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final OperationRepository operationRepository;
    private final OperationMapper operationMapper;

    @Value("${business.document.upload-path:./uploads}")
    private String uploadPath;

    @Value("${business.document.allowed-types:pdf,jpg,jpeg,png}")
    private String allowedTypes;

    @Override
    @Transactional
    public DocumentDto uploadDocument(Long operationId, MultipartFile file) {
        Operation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new ResourceNotFoundException("Operation not found"));

        validateFile(file);

        Path uploadDir = Paths.get(uploadPath);
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new InvalidOperationException("Could not create upload directory");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String newFilename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadDir.resolve(newFilename);

        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new InvalidOperationException("Could not save file: " + e.getMessage());
        }

        Document document = Document.builder()
                .name(originalFilename)
                .type(file.getContentType())
                .path(filePath.toString())
                .operation(operation)
                .build();

        Document savedDocument = documentRepository.save(document);
        return operationMapper.toDocumentDto(savedDocument);
    }

    @Override
    public List<DocumentDto> getDocumentsByOperationId(Long operationId) {
        List<Document> documents = documentRepository.findByOperationId(operationId);
        return operationMapper.toDocumentDtoList(documents);
    }

    @Override
    public byte[] downloadDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        try {
            Path filePath = Paths.get(document.getPath());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new InvalidOperationException("Could not read file: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidOperationException("File is empty");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new InvalidOperationException("File size exceeds maximum limit of 5MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new InvalidOperationException("File name is required");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowed = Arrays.asList(allowedTypes.split(","));
        if (!allowed.contains(extension)) {
            throw new InvalidOperationException("File type not allowed. Allowed types: " + allowedTypes);
        }
    }
}
