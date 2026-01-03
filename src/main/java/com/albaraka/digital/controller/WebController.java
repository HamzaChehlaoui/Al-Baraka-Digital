package com.albaraka.digital.controller;

import com.albaraka.digital.dto.request.OperationRequest;
import com.albaraka.digital.dto.response.OperationDto;
import com.albaraka.digital.model.User;
import com.albaraka.digital.model.enums.AiValidationStatus;
import com.albaraka.digital.model.enums.OperationType;
import com.albaraka.digital.repository.UserRepository;
import com.albaraka.digital.service.DocumentService;
import com.albaraka.digital.service.OperationService;
import com.albaraka.digital.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final OperationService operationService;
    private final UserService userService;
    private final DocumentService documentService;
    private final UserRepository userRepository;
    private final com.albaraka.digital.service.AccountService accountService;
    private final com.albaraka.digital.repository.OperationRepository operationRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return "redirect:/admin/dashboard";
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_AGENT"))) {
                return "redirect:/agent/dashboard";
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENT"))) {
                return "redirect:/client/dashboard";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/client/dashboard")
    public String clientDashboard(Model model) {
        User user = getCurrentUser();
        List<com.albaraka.digital.dto.response.OperationDto> operations = operationService
                .getOperationsByUserId(user.getId());

        // Get account balance
        List<com.albaraka.digital.dto.response.AccountDto> accounts = accountService.getAccountsByUserId(user.getId());
        BigDecimal balance = accounts.isEmpty() ? BigDecimal.ZERO : accounts.get(0).getBalance();

        // Calculate statistics
        long pendingCount = operations.stream()
                .filter(op -> op.getStatus() == com.albaraka.digital.model.enums.OperationStatus.PENDING)
                .count();

        long monthlyOps = operations.stream()
                .filter(op -> op.getDate().isAfter(java.time.LocalDateTime.now().minusMonths(1)))
                .count();

        model.addAttribute("operations", operations.subList(0, Math.min(5, operations.size())));
        model.addAttribute("accountBalance", balance);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("totalOperations", operations.size());
        model.addAttribute("monthlyOperations", monthlyOps);
        model.addAttribute("user", user);

        // Get account for sidebar
        if (!accounts.isEmpty()) {
            model.addAttribute("account", accounts.get(0));
        }

        return "client/dashboard";
    }

    @GetMapping("/client/operations")
    public String clientOperations(Model model) {
        User user = getCurrentUser();
        model.addAttribute("operations", operationService.getOperationsByUserId(user.getId()));
        model.addAttribute("user", user);
        return "client/operations";
    }

    @GetMapping("/client/profile")
    public String clientProfile(Model model) {
        User user = getCurrentUser();
        List<com.albaraka.digital.dto.response.AccountDto> accounts = accountService.getAccountsByUserId(user.getId());
        List<com.albaraka.digital.dto.response.OperationDto> operations = operationService
                .getOperationsByUserId(user.getId());

        long pendingCount = operations.stream()
                .filter(op -> op.getStatus() == com.albaraka.digital.model.enums.OperationStatus.PENDING)
                .count();

        model.addAttribute("user", user);
        model.addAttribute("account", accounts.isEmpty() ? null : accounts.get(0));
        model.addAttribute("totalOperations", operations.size());
        model.addAttribute("pendingCount", pendingCount);

        return "client/profile";
    }

    @PostMapping("/client/operations/create")
    public String createOperation(@RequestParam("type") OperationType type,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        User user = getCurrentUser();

        OperationRequest request = OperationRequest.builder()
                .type(type)
                .amount(amount)
                .description(description)
                .build();

        OperationDto operation = operationService.createOperation(user.getId(), request);

        if (file != null && !file.isEmpty()) {
            documentService.uploadDocument(operation.getId(), file);
        }

        return "redirect:/client/dashboard";
    }

    @GetMapping("/agent/dashboard")
    public String agentDashboard(Model model) {
        User user = getCurrentUser();
        List<com.albaraka.digital.dto.response.OperationDto> pendingOps = operationService.getPendingOperations();

        // Calculate today's statistics
        java.time.LocalDateTime startOfDay = java.time.LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        List<com.albaraka.digital.model.Operation> allOps = operationRepository.findAll();

        long approvedToday = allOps.stream()
                .filter(op -> op.getStatus() == com.albaraka.digital.model.enums.OperationStatus.APPROVED &&
                        op.getDate().isAfter(startOfDay))
                .count();

        long rejectedToday = allOps.stream()
                .filter(op -> op.getStatus() == com.albaraka.digital.model.enums.OperationStatus.REJECTED &&
                        op.getDate().isAfter(startOfDay))
                .count();

        model.addAttribute("pendingOperations", pendingOps.subList(0, Math.min(5, pendingOps.size())));
        model.addAttribute("priorityOperations", pendingOps);
        model.addAttribute("pendingCount", pendingOps.size());
        model.addAttribute("approvedToday", approvedToday);
        model.addAttribute("rejectedToday", rejectedToday);
        model.addAttribute("aiApproved", 0); // TODO: Track AI approvals
        model.addAttribute("user", user);

        return "agent/dashboard";
    }

    @GetMapping("/agent/pending")
    public String agentPending(Model model) {
        User user = getCurrentUser();
        model.addAttribute("pendingOperations", operationService.getPendingOperations());
        model.addAttribute("user", user);
        return "agent/pending";
    }

    @GetMapping("/agent/operations/{id}")
    public String agentOperationDetails(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        User user = getCurrentUser();
        com.albaraka.digital.dto.response.OperationDto operation = operationService.getOperationById(id);

        // TODO: Get AI validation result if available
        // For now, we'll pass null and the page will handle it

        model.addAttribute("operation", operation);
        model.addAttribute("aiValidation", null); // TODO: Implement AI validation retrieval
        model.addAttribute("user", user);

        return "agent/operation-details";
    }

    @PostMapping("/agent/operations/review")
    public String reviewOperation(@RequestParam("operationId") Long operationId,
            @RequestParam("action") String action) {
        if ("APPROVE".equalsIgnoreCase(action)) {
            operationService.approveOperation(operationId);
        } else if ("REJECT".equalsIgnoreCase(action)) {
            operationService.rejectOperation(operationId);
        }
        return "redirect:/agent/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        User user = getCurrentUser();
        List<com.albaraka.digital.dto.response.UserDto> users = userService.getAllUsers();
        List<com.albaraka.digital.model.Operation> allOps = operationRepository.findAll();

        // Calculate today's operations
        java.time.LocalDateTime startOfDay = java.time.LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        long operationsToday = allOps.stream()
                .filter(op -> op.getDate().isAfter(startOfDay))
                .count();

        long approvedToday = allOps.stream()
                .filter(op -> op.getStatus() == com.albaraka.digital.model.enums.OperationStatus.APPROVED &&
                        op.getDate().isAfter(startOfDay))
                .count();

        long rejectedToday = allOps.stream()
                .filter(op -> op.getStatus() == com.albaraka.digital.model.enums.OperationStatus.REJECTED &&
                        op.getDate().isAfter(startOfDay))
                .count();

        long pendingCount = allOps.stream()
                .filter(op -> op.getStatus() == com.albaraka.digital.model.enums.OperationStatus.PENDING)
                .count();

        model.addAttribute("users", users.subList(0, Math.min(5, users.size())));
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("totalAccounts", users.size()); // Simplified: 1 account per user
        model.addAttribute("operationsToday", operationsToday);
        model.addAttribute("pendingOperations", pendingCount);
        model.addAttribute("approvedToday", approvedToday);
        model.addAttribute("rejectedToday", rejectedToday);
        model.addAttribute("aiAutoApproved", 0); // TODO: Track AI approvals
        model.addAttribute("user", user);

        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String adminUsers(Model model) {
        User user = getCurrentUser();
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("user", user);
        return "admin/users";
    }

    @GetMapping("/admin/statistics")
    public String adminStatistics(Model model) {
        User user = getCurrentUser();
        List<com.albaraka.digital.model.Operation> allOps = operationRepository.findAll();

        // Operations by type
        long depositCount = allOps.stream()
                .filter(op -> op.getType() == com.albaraka.digital.model.enums.OperationType.DEPOSIT)
                .count();
        long withdrawalCount = allOps.stream()
                .filter(op -> op.getType() == com.albaraka.digital.model.enums.OperationType.WITHDRAWAL)
                .count();
        long transferCount = allOps.stream()
                .filter(op -> op.getType() == com.albaraka.digital.model.enums.OperationType.TRANSFER)
                .count();

        // Operations by status
        long approvedCount = allOps.stream()
                .filter(op -> op.getStatus() == com.albaraka.digital.model.enums.OperationStatus.APPROVED)
                .count();
        long pendingCount = allOps.stream()
                .filter(op -> op.getStatus() == com.albaraka.digital.model.enums.OperationStatus.PENDING)
                .count();
        long rejectedCount = allOps.stream()
                .filter(op -> op.getStatus() == com.albaraka.digital.model.enums.OperationStatus.REJECTED)
                .count();

        // Calculate total volume
        BigDecimal totalVolume = allOps.stream()
                .map(com.albaraka.digital.model.Operation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("user", user);
        model.addAttribute("totalOperations", allOps.size());
        model.addAttribute("depositCount", depositCount);
        model.addAttribute("withdrawalCount", withdrawalCount);
        model.addAttribute("transferCount", transferCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("rejectedCount", rejectedCount);
        model.addAttribute("totalVolume", totalVolume);
        model.addAttribute("aiEfficiency", 0); // TODO: Calculate from AI validation data
        model.addAttribute("aiApprovedCount", 0); // TODO: Track AI approvals
        model.addAttribute("aiRejectedCount", 0); // TODO: Track AI rejections
        model.addAttribute("aiReviewCount", 0); // TODO: Track AI reviews needed

        return "admin/statistics";
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
