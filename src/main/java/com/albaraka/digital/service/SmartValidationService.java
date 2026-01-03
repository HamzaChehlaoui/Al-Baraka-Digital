package com.albaraka.digital.service;

import com.albaraka.digital.dto.ai.AiValidationResult;
import org.springframework.core.io.Resource;

public interface SmartValidationService {
    AiValidationResult validateDocument(Resource document, String operationDetails);
}
