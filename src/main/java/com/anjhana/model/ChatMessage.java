package com.anjhana.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    private MessageRole role;  // USER, ASSISTANT

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private Integer tokensUsed;
}
