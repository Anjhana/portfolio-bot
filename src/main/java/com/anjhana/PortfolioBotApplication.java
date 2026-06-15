package com.anjhana;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * Architecture:
 *   User Query → Embed → Vector Search → Context → LLM Prompt → Response
 *
 * Technologies:
 *   Spring AI (ChatClient, VectorStore)
 *   LangChain4j (alternative AI SDK)
 *   pgvector (PostgreSQL vector extension for embeddings)
 *   OpenAI (LLM)
 *
 */
@SpringBootApplication
public class PortfolioBotApplication {
    public static void main(String[] args) { SpringApplication.run(PortfolioBotApplication.class, args); }
}
