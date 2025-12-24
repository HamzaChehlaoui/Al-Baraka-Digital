package com.albaraka.digital.controller;

import com.albaraka.digital.dto.response.DocumentDto;
import com.albaraka.digital.dto.response.OperationDto;
import com.albaraka.digital.service.DocumentService;
import com.albaraka.digital.service.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final OperationService operationService;
    private final DocumentService documentService;

    @GetMapping("/operations/pending")
    public ResponseEntity<List<OperationDto>> getPendingOperations() {
        return ResponseEntity.ok(operationService.getPendingOperations());
    }

    @GetMapping("/operations/{id}")
    public ResponseEntity<OperationDto> getOperation(@PathVariable Long id) {
        return ResponseEntity.ok(operationService.getOperationById(id));
    }

    @GetMapping("/operations/{id}/documents")
    public ResponseEntity<List<DocumentDto>> getOperationDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentsByOperationId(id));
    }

    @PutMapping("/operations/{id}/approve")
    public ResponseEntity<OperationDto> approveOperation(@PathVariable Long id) {
        return ResponseEntity.ok(operationService.approveOperation(id));
    }

    @PutMapping("/operations/{id}/reject")
    public ResponseEntity<OperationDto> rejectOperation(@PathVariable Long id) {
        return ResponseEntity.ok(operationService.rejectOperation(id));
    }
}
