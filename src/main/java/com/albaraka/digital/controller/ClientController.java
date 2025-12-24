package com.albaraka.digital.controller;

import com.albaraka.digital.dto.request.OperationRequest;
import com.albaraka.digital.dto.response.AccountDto;
import com.albaraka.digital.dto.response.DocumentDto;
import com.albaraka.digital.dto.response.OperationDto;
import com.albaraka.digital.model.User;
import com.albaraka.digital.repository.UserRepository;
import com.albaraka.digital.service.AccountService;
import com.albaraka.digital.service.DocumentService;
import com.albaraka.digital.service.OperationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {

    private final OperationService operationService;
    private final AccountService accountService;
    private final DocumentService documentService;
    private final UserRepository userRepository;

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDto>> getMyAccounts() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(accountService.getAccountsByUserId(userId));
    }

    @GetMapping("/operations")
    public ResponseEntity<List<OperationDto>> getMyOperations() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(operationService.getOperationsByUserId(userId));
    }

    @PostMapping("/operations")
    public ResponseEntity<OperationDto> createOperation(@Valid @RequestBody OperationRequest request) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(operationService.createOperation(userId, request));
    }

    @GetMapping("/operations/{id}")
    public ResponseEntity<OperationDto> getOperation(@PathVariable Long id) {
        return ResponseEntity.ok(operationService.getOperationById(id));
    }

    @PostMapping("/operations/{id}/document")
    public ResponseEntity<DocumentDto> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(documentService.uploadDocument(id, file));
    }

    @GetMapping("/operations/{id}/documents")
    public ResponseEntity<List<DocumentDto>> getDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentsByOperationId(id));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}
