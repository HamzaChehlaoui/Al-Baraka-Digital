package com.albaraka.digital.service.impl;

import com.albaraka.digital.dto.ai.AiValidationResult;
import com.albaraka.digital.model.enums.AiValidationStatus;
import com.albaraka.digital.service.SmartValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.Media;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmartValidationServiceImpl implements SmartValidationService {

    private final ChatModel chatClient;

    @Override
    public AiValidationResult validateDocument(Resource document, String operationDetails) {
        log.info("Starting AI validation for document...");
        try {
            // Using a simple prompt + image
            String promptText = """
                    You are a bank compliance agent. Analyze the attached document and the following operation details:
                    %s

                    Task:
                    1. Check if the document matches the operation details (amount, logic).
                    2. Check if the document looks authentic and legible.

                    Return a JSON response in the following format (do not use markdown code blocks):
                    {
                        "status": "APPROVE" | "REJECT" | "NEED_HUMAN_REVIEW",
                        "reasoning": "Explanation of the decision",
                        "confidence": 0.0 to 1.0
                    }

                    Rules:
                    - If confident > 0.9 and document is valid -> APPROVE
                    - If document is clearly fake or invalid -> REJECT
                    - Otherwise -> NEED_HUMAN_REVIEW
                    """.formatted(operationDetails);

            UserMessage userMessage = new UserMessage(promptText,
                    List.of(new Media(MimeTypeUtils.IMAGE_JPEG, document)));
            Prompt prompt = new Prompt(userMessage);

            String response = chatClient.call(prompt).getResult().getOutput().getContent();
            log.info("AI Response: {}", response);

            return parseResponse(response);

        } catch (Exception e) {
            log.error("AI Validation failed", e);
            return AiValidationResult.builder()
                    .status(AiValidationStatus.NEED_HUMAN_REVIEW)
                    .reasoning("AI Analysis failed: " + e.getMessage())
                    .confidence(0.0)
                    .build();
        }
    }

    private AiValidationResult parseResponse(String json) {
        // Simple manual parsing or use Jackson.
        // For robustness, assuming the AI returns valid JSON or doing simple cleaning.
        String cleanJson = json.replace("```json", "").replace("```", "").trim();

        try {
            // In a real app, use ObjectMapper. Here, we'll do simple extraction for
            // robustness against bad formatting
            // or better, use ObjectMapper if dependencies are available.
            // We have Jackson.
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(cleanJson, AiValidationResult.class);
        } catch (Exception e) {
            log.error("Failed to parse AI response: " + json, e);
            return AiValidationResult.builder()
                    .status(AiValidationStatus.NEED_HUMAN_REVIEW)
                    .reasoning("Failed to parse AI response")
                    .confidence(0.0)
                    .build();
        }
    }
}
