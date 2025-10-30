package com.example.aibackend.controller;

import com.example.aibackend.repository.ChatRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Note: @Controller, NOT @RestController
public class PageController {

    private final ChatRepository chatRepository;

    public PageController(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        // 1. Fetch all chat history from the database
        // 2. Add it to the "model" so Thymeleaf can access it
        model.addAttribute("chatHistory", chatRepository.findAll());
        
        // 3. Return the name of the HTML file (index.html)
        return "index";
    }
}