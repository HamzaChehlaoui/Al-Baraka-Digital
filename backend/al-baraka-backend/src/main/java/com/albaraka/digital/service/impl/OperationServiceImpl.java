package com.albaraka.digital.service.impl;

import com.albaraka.digital.dto.request.OperationRequest;
import com.albaraka.digital.dto.response.OperationDto;
import com.albaraka.digital.exception.InsufficientBalanceException;
import com.albaraka.digital.exception.InvalidOperationException;
import com.albaraka.digital.exception.ResourceNotFoundException;
import com.albaraka.digital.mapper.OperationMapper;
import com.albaraka.digital.model.Account;
import com.albaraka.digital.model.Operation;
import com.albaraka.digital.model.enums.OperationStatus;
import com.albaraka.digital.model.enums.OperationType;
import com.albaraka.digital.repository.AccountRepository;
import com.albaraka.digital.repository.OperationRepository;
import com.albaraka.digital.service.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OperationServiceImpl implements OperationService {

    private final OperationRepository operationRepository;
    private final AccountRepository accountRepository;
    private final OperationMapper operationMapper;

    @Value("${business.operation.auto-validation-threshold:10000}")
    private BigDecimal autoValidationThreshold;

    @Override
    public OperationDto createOperation(Long userId, OperationRequest request) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        if (accounts.isEmpty()) {
            throw new ResourceNotFoundException("No account found for user");
        }
        Account sourceAccount = accounts.get(0);

        validateOperation(request, sourceAccount);

        Operation operation = Operation.builder()
                .type(request.getType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .account(sourceAccount)
                .build();

        boolean autoValidate = request.getAmount().compareTo(autoValidationThreshold) <= 0;

        if (autoValidate) {
            operation.setStatus(OperationStatus.APPROVED);
            executeOperation(operation, request);
        } else {
            operation.setStatus(OperationStatus.PENDING);
        }

        Operation savedOperation = operationRepository.save(operation);
        return operationMapper.toDto(savedOperation);
    }

    @Override
    public List<OperationDto> getOperationsByUserId(Long userId) {
        List<Operation> operations = operationRepository.findByAccountUserId(userId);
        return operationMapper.toDtoList(operations);
    }

    @Override
    public List<OperationDto> getPendingOperations() {
        List<Operation> operations = operationRepository.findByStatusOrderByDateAsc(OperationStatus.PENDING);
        return operationMapper.toDtoList(operations);
    }

    @Override
    public OperationDto approveOperation(Long operationId) {
        Operation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new ResourceNotFoundException("Operation not found"));

        if (operation.getStatus() != OperationStatus.PENDING) {
            throw new InvalidOperationException("Operation is not in PENDING status");
        }


        if (operation.getType() == OperationType.WITHDRAWAL || operation.getType() == OperationType.TRANSFER) {
            Account account = operation.getAccount();
            if (account.getBalance().compareTo(operation.getAmount()) < 0) {
                throw new InsufficientBalanceException("Insufficient balance to approve this operation");
            }
        }

        OperationRequest request = OperationRequest.builder()
                .type(operation.getType())
                .amount(operation.getAmount())
                .description(operation.getDescription())
                .build();
        executeOperation(operation, request);

        operation.setStatus(OperationStatus.APPROVED);
        return operationMapper.toDto(operationRepository.save(operation));
    }

    @Override
    public OperationDto rejectOperation(Long operationId) {
        Operation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new ResourceNotFoundException("Operation not found"));

        if (operation.getStatus() != OperationStatus.PENDING) {
            throw new InvalidOperationException("Operation is not in PENDING status");
        }

        operation.setStatus(OperationStatus.REJECTED);
        return operationMapper.toDto(operationRepository.save(operation));
    }

    @Override
    public OperationDto getOperationById(Long operationId) {
        Operation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new ResourceNotFoundException("Operation not found"));
        return operationMapper.toDto(operation);
    }

    private void validateOperation(OperationRequest request, Account sourceAccount) {
        switch (request.getType()) {
            case WITHDRAWAL:
                if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
                    throw new InsufficientBalanceException("Insufficient balance for withdrawal");
                }
                break;
            case TRANSFER:
                if (request.getTargetAccountNumber() == null || request.getTargetAccountNumber().isBlank()) {
                    throw new InvalidOperationException("Target account number is required for transfer");
                }
                if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
                    throw new InsufficientBalanceException("Insufficient balance for transfer");
                }
                if (!accountRepository.findByAccountNumber(request.getTargetAccountNumber()).isPresent()) {
                    throw new ResourceNotFoundException(
                            "Target account not found: " + request.getTargetAccountNumber());
                }
                break;
            case DEPOSIT:
                break;
        }
    }

    private void executeOperation(Operation operation, OperationRequest request) {
        Account account = operation.getAccount();

        switch (operation.getType()) {
            case DEPOSIT:
                account.setBalance(account.getBalance().add(operation.getAmount()));
                accountRepository.save(account);
                break;
            case WITHDRAWAL:
                account.setBalance(account.getBalance().subtract(operation.getAmount()));
                accountRepository.save(account);
                break;
            case TRANSFER:
                account.setBalance(account.getBalance().subtract(operation.getAmount()));
                accountRepository.save(account);

                if (request.getTargetAccountNumber() != null) {
                    Account targetAccount = accountRepository.findByAccountNumber(request.getTargetAccountNumber())
                            .orElseThrow(() -> new ResourceNotFoundException("Target account not found"));
                    targetAccount.setBalance(targetAccount.getBalance().add(operation.getAmount()));
                    accountRepository.save(targetAccount);
                }
                break;
        }
    }
}
