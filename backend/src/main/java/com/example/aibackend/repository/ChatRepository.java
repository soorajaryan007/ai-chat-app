package com.example.aibackend.repository;

import com.example.aibackend.model.ChatEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends MongoRepository<ChatEntry, String> {
    // You can add custom find methods here later if needed, e.g.:
    // List<ChatEntry> findByTimestampAfter(LocalDateTime date);
}