package com.albaraka.digital.dto.ai;

import com.albaraka.digital.model.enums.AiValidationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiValidationResult {
    private AiValidationStatus status;
    private String reasoning;
    private double confidence;
}
