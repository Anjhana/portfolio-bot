package com.anjhana.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.ai.document.Document;

public class ChatDtos {

    public record ChatRequest(
        @NotBlank String sessionId,
        @NotBlank String message
    ) {}

    public record ChatResponse(
        String sessionId,
        String userMessage,
        String botResponse,
        List<String> sourcesUsed,
        int tokensUsed,
        LocalDateTime timestamp
    ) {}

    public record IngestRequest(
        @NotBlank String title,
        @NotBlank List<Document> content,
        String category
    ) {}

    public record SessionHistory(
        String sessionId,
        List<MessageDto> messages
    ) {}

    public record MessageDto(
        String role,
        String content,
        LocalDateTime timestamp
    ) {}
}
