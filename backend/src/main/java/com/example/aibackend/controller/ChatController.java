package com.example.aibackend.controller;

import com.example.aibackend.dto.ChatRequest;
import com.example.aibackend.model.ChatEntry;
import com.example.aibackend.repository.ChatRepository;
import com.example.aibackend.service.ChatService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
// Allow requests from a React frontend running on localhost:3000
@CrossOrigin(origins = "http://localhost:3000") 
public class ChatController {

    private final ChatService chatService;
    private final ChatRepository chatRepository;

    public ChatController(ChatService chatService, ChatRepository chatRepository) {
        this.chatService = chatService;
        this.chatRepository = chatRepository;
    }

    /**
     * The main endpoint to ask a question.
     * It gets an answer from the AI, saves both to the DB,
     * and returns the saved entry (question, answer, timestamp).
     */
    @PostMapping
    public Mono<ChatEntry> processMessage(@RequestBody ChatRequest request) {
        return chatService.getAiResponseAndSave(request.getQuestion());
    }

    /**
     * An extra endpoint to get all past conversations.
     */
    @GetMapping("/history")
    public List<ChatEntry> getHistory() {
        return chatRepository.findAll();
    }
}