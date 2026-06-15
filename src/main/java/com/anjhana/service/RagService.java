package com.anjhana.service;

import com.anjhana.dto.ChatDtos;
import com.anjhana.dto.ChatDtos.*;
import com.anjhana.model.ChatMessage;
import com.anjhana.model.KnowledgeDocument;
import com.anjhana.model.MessageRole;
import com.anjhana.repository.ChatMessageRepository;
import com.anjhana.repository.KnowledgeDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RAG (Retrieval Augmented Generation) Service.
 *
 * Flow for every user query:
 *   1. Embed query → search vector store for relevant docs (similarity search)
 *   2. Build context string from top-3 matching chunks
 *   3. Send context + user query to LLM as a structured prompt
 *   4. LLM answers ONLY from provided context (prevents hallucination)
 *   5. Store conversation in DB for history tracking
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ChatMessageRepository chatRepo;
    private final KnowledgeDocumentRepository docRepo;

    

    // ── CHAT ────────────────────────────────────────────────────────────────

    public ChatResponse chat(ChatRequest request) {
        log.info("Chat request sessionId: {} message: {}", request.sessionId(),
                 request.message().substring(0, Math.min(50, request.message().length())));

        // Step 1: Retrieve relevant context from vector store
        List<Document> relevantDocs = vectorStore.similaritySearch(request.message());
        String context = relevantDocs.stream()
            .map(Document::getText)
            .collect(Collectors.joining("\n\n---\n\n"));

        List<String> sources = relevantDocs.stream()
            .map(d -> (String) d.getMetadata().getOrDefault("title", "Unknown"))
            .distinct()
            .collect(Collectors.toList());

        // Step 2: Build prompt with context
        String promptText = String.format(
            "Context from knowledge base:\n%s\n\nCustomer question: %s",
            context.isEmpty() ? "No relevant context found." : context,
            request.message()
        );

        // Step 3: Call LLM
        Prompt prompt = new Prompt(List.of(
            new UserMessage(promptText)
        ));

        String botResponse = chatClient.prompt(prompt).call().content();

        // Step 4: Persist conversation
        saveChatMessage(request.sessionId(), MessageRole.USER, request.message(), 0);
        saveChatMessage(request.sessionId(), MessageRole.ASSISTANT, botResponse, 0);

        log.info("Chat response for sessionId: {} sources: {}", request.sessionId(), sources);

        return new ChatResponse(
            request.sessionId(),
            request.message(),
            botResponse,
            sources,
            promptText.length() / 4,  // rough token estimate
            LocalDateTime.now()
        );
    }

    // ── KNOWLEDGE BASE MANAGEMENT ───────────────────────────────────────────

    /**
     * Ingest a new document into the vector store.
     * Documents are chunked and embedded for semantic search.
     */
    public void ingestDocument(IngestRequest request) {
        log.info("Ingesting document: {} category: {}", request.title(), request.category());
        for (Document doclist : request.content()) {
        // Save to relational DB
        KnowledgeDocument doc = KnowledgeDocument.builder()
            .title(request.title())
            .content(doclist.getText())
            .category(request.category())
            .build();
        docRepo.save(doc);

        // Add to vector store for semantic search
        Document vectorDoc = new Document(
            doclist.getText(),
            java.util.Map.of("title", request.title(), "category", request.category())
        );
        
        vectorStore.add(List.of(vectorDoc));
        }

        log.info("Document ingested successfully: {}", request.title());
    }

    // ── SESSION HISTORY ─────────────────────────────────────────────────────

    public SessionHistory getHistory(String sessionId) {
        List<ChatMessage> messages = chatRepo.findBySessionIdOrderByTimestampAsc(sessionId);
        var msgDtos = messages.stream()
            .map(m -> new ChatDtos.MessageDto(m.getRole().name(), m.getContent(), m.getTimestamp()))
            .collect(Collectors.toList());
        return new SessionHistory(sessionId, msgDtos);
    }

    // ── PRIVATE ─────────────────────────────────────────────────────────────

    private void saveChatMessage(String sessionId, MessageRole role, String content, int tokens) {
        chatRepo.save(ChatMessage.builder()
            .sessionId(sessionId).role(role)
            .content(content).tokensUsed(tokens).build());
    }
}
