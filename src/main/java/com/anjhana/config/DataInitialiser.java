package com.anjhana.config;

import com.anjhana.dto.ChatDtos.IngestRequest;
import com.anjhana.service.RagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Seeds the knowledge base on startup with FAQ documents.
 * In production: load from a database or file system instead.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitialiser {

    @Bean
    public CommandLineRunner seedKnowledgeBase(RagService ragService) {
        return args -> {
            log.info("Seeding knowledge base with FAQ documents...");
            Resource pdfResource = new FileSystemResource("$Resume_path");
            TikaDocumentReader reader = new TikaDocumentReader(pdfResource);
            String title = pdfResource.getFilename();
            // 2. Extract text into Spring AI Documents
            ragService.ingestDocument(new IngestRequest(
                title,
                reader.get(),
                "portfolio"
            ));
            log.info("Knowledge base seeded with documents.");
        };
    }
}
