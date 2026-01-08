package com.albaraka.digital.service;

import com.albaraka.digital.dto.request.OperationRequest;
import com.albaraka.digital.dto.response.OperationDto;

import java.util.List;

public interface OperationService {

    OperationDto createOperation(Long userId, OperationRequest request);

    List<OperationDto> getOperationsByUserId(Long userId);

    List<OperationDto> getPendingOperations();

    OperationDto approveOperation(Long operationId);

    OperationDto rejectOperation(Long operationId);

    OperationDto getOperationById(Long operationId);
}
