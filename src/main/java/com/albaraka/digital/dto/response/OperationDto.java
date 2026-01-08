package com.albaraka.digital.dto.response;

import com.albaraka.digital.model.enums.OperationStatus;
import com.albaraka.digital.model.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationDto {

    private Long id;
    private OperationType type;
    private BigDecimal amount;
    private OperationStatus status;
    private LocalDateTime date;
    private String description;
    private String accountNumber;
    private String targetAccountNumber;
    private String ownerName;
    private List<DocumentDto> documents;
}
