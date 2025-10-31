package com.example.aibackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String googleId; // The unique ID from Google

    @Indexed(unique = true) // No two users can have the same email
    private String email;

    private String name;
    private String pictureUrl;
}