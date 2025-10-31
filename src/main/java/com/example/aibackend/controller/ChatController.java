package com.example.aibackend.controller;

import com.example.aibackend.dto.ChatRequest;
import com.example.aibackend.model.ChatEntry;
import com.example.aibackend.model.User;
import com.example.aibackend.repository.ChatRepository;
import com.example.aibackend.repository.UserRepository;
import com.example.aibackend.service.ChatService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository; // We need this to find our internal User ID
    private final ChatRepository chatRepository;

    public ChatController(ChatService chatService, UserRepository userRepository, ChatRepository chatRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    @PostMapping("/{conversationId}")
    public Mono<ChatEntry> processMessage(
            @PathVariable String conversationId,
            @RequestBody ChatRequest request,
            @AuthenticationPrincipal OidcUser principal // Get the logged-in user
    ) {
        // Find our internal User by their Google ID
        User user = userRepository.findByGoogleId(principal.getSubject())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Call the service with all the required IDs
        return chatService.getAiResponseAndSave(user.getId(), conversationId, request.getQuestion());
    }

    @GetMapping("/history")
    public List<ChatEntry> getHistory() {
        return chatRepository.findAll();
    }
}