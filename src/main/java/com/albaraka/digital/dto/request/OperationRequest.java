package com.albaraka.digital.dto.request;

import com.albaraka.digital.model.enums.OperationType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationRequest {

    @NotNull(message = "Operation type is required")
    private OperationType type;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    // Required only for TRANSFER operations
    private String targetAccountNumber;

    private String description;
}
