package com.anjhana.controller;

import com.anjhana.dto.ChatDtos.*;
import com.anjhana.service.RagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API for the Smart Support Bot.
 *
 * POST /api/chat           - Send a message and get AI response
 * POST /api/chat/ingest    - Add document to knowledge base
 * GET  /api/chat/history/{sessionId} - Get conversation history
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final RagService ragService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(ragService.chat(request));
    }

    @PostMapping("/ingest")
    public ResponseEntity<Void> ingest(@Valid @RequestBody IngestRequest request) {
        ragService.ingestDocument(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<SessionHistory> getHistory(@PathVariable String sessionId) {
        return ResponseEntity.ok(ragService.getHistory(sessionId));
    }
}
