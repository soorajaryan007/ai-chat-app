package com.example.aibackend.controller;

import com.example.aibackend.model.ChatEntry;
import com.example.aibackend.model.User;
import com.example.aibackend.repository.ChatRepository;
import com.example.aibackend.repository.UserRepository;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class PageController {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository; // We need this to find our internal User ID

    public PageController(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    /**
     * This is now the "lobby" page (the new homepage).
     * It shows a list of all past conversations FOR THE LOGGED-IN USER.
     */
    @GetMapping("/")
    public String lobby(Model model, @AuthenticationPrincipal OidcUser principal) {
        
        if (principal != null) {
            // --- USER IS LOGGED IN ---
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("userName", principal.getFullName());
            model.addAttribute("pictureUrl", principal.getPicture());
            
            // Find our internal User by their Google ID
            User user = userRepository.findByGoogleId(principal.getSubject())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 1. Get all chat entries for THIS user
            List<ChatEntry> allEntries = chatRepository.findByUserIdOrderByTimestampAsc(user.getId());

            // 2. Group them by conversationId
            Map<String, ChatEntry> conversations = allEntries.stream()
                    .sorted(Comparator.comparing(ChatEntry::getTimestamp)) 
                    .collect(Collectors.toMap(
                            ChatEntry::getConversationId, 
                            entry -> entry,                
                            (existing, replacement) -> existing 
                    ));
            model.addAttribute("conversations", conversations.values());

        } else {
            // --- USER IS NOT LOGGED IN ---
            model.addAttribute("isLoggedIn", false);
            // THIS IS THE FIX: Add an empty list so Thymeleaf doesn't crash
            model.addAttribute("conversations", List.of());
        }
        
        return "lobby";
    }
    /**
     * This endpoint handles the "New Chat" button.
     */
    @GetMapping("/chat/new")
    public String newChat() {
        String newConversationId = UUID.randomUUID().toString();
        return "redirect:/chat/" + newConversationId;
    }

    /**
     * This is the main chat page.
     * It loads a *specific* conversation based on its ID AND the user.
     */
    @GetMapping("/chat/{conversationId}")
    public String chatPage(
            @PathVariable String conversationId,
            Model model,
            @AuthenticationPrincipal OidcUser principal
    ) {
        
        if (principal != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("userName", principal.getFullName());
            model.addAttribute("pictureUrl", principal.getPicture());

            // Find our internal User by their Google ID
            User user = userRepository.findByGoogleId(principal.getSubject())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 1. Fetch only the chat history for THIS user AND THIS conversation
            List<ChatEntry> chatHistory = chatRepository.findByUserIdAndConversationIdOrderByTimestampAsc(user.getId(), conversationId);
            
            model.addAttribute("chatHistory", chatHistory);
            model.addAttribute("conversationId", conversationId);
            
        } else {
             model.addAttribute("isLoggedIn", false);
        }
        
        // Return the "index.html" template (our main chat UI)
        return "index";
    }
}