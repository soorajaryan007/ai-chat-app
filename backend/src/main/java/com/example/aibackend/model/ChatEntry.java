package com.example.aibackend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

// Using Lombok for getters, setters, and constructors
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "chat_history") // This is the name of the collection in MongoDB
public class ChatEntry {

    @Id
    private String id;

    private String question;
    private String answer;
    private LocalDateTime timestamp;

    public ChatEntry(String question, String answer) {
        this.question = question;
        this.answer = answer;
        this.timestamp = LocalDateTime.now();
    }
}