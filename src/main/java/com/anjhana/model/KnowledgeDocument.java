package com.anjhana.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "knowledge_documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KnowledgeDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String category;  // FAQ, POLICY, PRODUCT

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
