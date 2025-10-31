package com.example.aibackend.repository;

import com.example.aibackend.model.ChatEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Import this

@Repository
public interface ChatRepository extends MongoRepository<ChatEntry, String> {
    
    // Finds all chats for a specific user, in a specific conversation
    List<ChatEntry> findByUserIdAndConversationIdOrderByTimestampAsc(String userId, String conversationId);
    
    // Finds all chats for a user (so we can build the lobby)
    List<ChatEntry> findByUserIdOrderByTimestampAsc(String userId);
}