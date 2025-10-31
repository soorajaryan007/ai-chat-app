package com.example.aibackend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "chat_history")
public class ChatEntry {

    @Id
    private String id;

    private String conversationId; // For grouping messages
    private String userId;         // To link to the User
    
    private String question;
    private String answer;
    private LocalDateTime timestamp;

    public ChatEntry(String userId, String conversationId, String question, String answer) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.question = question;
        this.answer = answer;
        this.timestamp = LocalDateTime.now();
    }
}