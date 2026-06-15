package com.anjhana.repository;
import com.anjhana.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findBySessionIdOrderByTimestampAsc(String sessionId);
}
