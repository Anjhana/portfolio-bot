package com.anjhana.repository;
import com.anjhana.model.KnowledgeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocument, String> {
    List<KnowledgeDocument> findByCategory(String category);
}
