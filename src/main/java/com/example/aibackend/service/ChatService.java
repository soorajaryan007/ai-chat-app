package com.example.aibackend.service;

import com.example.aibackend.model.ChatEntry;
import com.example.aibackend.repository.ChatRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ChatService {

    private final AIService aiService;
    private final ChatRepository chatRepository;

    public ChatService(AIService aiService, ChatRepository chatRepository) {
        this.aiService = aiService;
        this.chatRepository = chatRepository;
    }

    // Updated to accept userId and conversationId
    public Mono<ChatEntry> getAiResponseAndSave(String userId, String conversationId, String question) {
        // 1. Get the response from the AI service
        return aiService.getMistralResponse(question)
                .flatMap(answer -> {
                    // 2. Once the answer is received, create the ChatEntry
                    ChatEntry entry = new ChatEntry(userId, conversationId, question, answer);
                    
                    // 3. Save it to MongoDB
                    ChatEntry savedEntry = chatRepository.save(entry);
                    
                    // 4. Return the saved entry
                    return Mono.just(savedEntry);
                });
    }
}