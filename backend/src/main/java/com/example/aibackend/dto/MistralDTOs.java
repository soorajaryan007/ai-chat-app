package com.example.aibackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

// --- Request DTOs ---
// These classes model the JSON we will SEND to Mistral
public class MistralDTOs {

    // The main request body
    @Data
    public static class MistralRequest {
        private String model;
        private List<Message> messages;

        public MistralRequest(String model, String userContent) {
            this.model = model;
            this.messages = List.of(new Message("user", userContent));
        }
    }

    // A message part of the request
    @Data
    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }


    // --- Response DTOs ---
    // These classes model the JSON we RECEIVE from Mistral
    @Data
    public static class MistralResponse {
        private String id;
        private String object;
        private long created;
        private String model;
        private List<Choice> choices;
    }

    @Data
    public static class Choice {
        private int index;
        @JsonProperty("message") // Maps the JSON "message" field to this "choiceMessage" variable
        private ChoiceMessage choiceMessage;
    }

    @Data
    public static class ChoiceMessage {
        private String role;
        private String content; // This is the AI's answer!
    }
}