package com.example.aibackend.service;

import com.example.aibackend.dto.MistralDTOs;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class AIService {

    private final WebClient mistralWebClient;

    // Spring injects the WebClient bean we created in WebClientConfig
    public AIService(WebClient mistralWebClient) {
        this.mistralWebClient = mistralWebClient;
    }

    public Mono<String> getMistralResponse(String prompt) {
        // 1. Create the request body
        // We'll hard-code the model name here
        MistralDTOs.MistralRequest requestBody = new MistralDTOs.MistralRequest(
    "mistral-medium-latest", // <-- Use this model name
    prompt
);

        // 2. Make the API call
        return mistralWebClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(MistralDTOs.MistralResponse.class)
                .map(response -> {
                    // 3. Extract the answer from the complex JSON response
                    if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                        return response.getChoices().get(0).getChoiceMessage().getContent();
                    }
                    return "Sorry, I couldn't get a response.";
                })
                .onErrorResume(e -> {
    // 4. Handle errors
    System.err.println("Error calling Mistral API: " + e.getMessage());

    // ADD THIS BLOCK TO LOG THE DETAILED ERROR
    if (e instanceof WebClientResponseException) {
        WebClientResponseException ex = (WebClientResponseException) e;
        System.err.println("Error Body: " + ex.getResponseBodyAsString());
    }

    return Mono.just("Error processing your request.");
});
    }
}