package com.example.aibackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${mistral.api.key}")
    private String mistralApiKey;

    @Value("${mistral.api.url}")
    private String mistralApiUrl;

    @Bean
    public WebClient mistralWebClient() {
        return WebClient.builder()
                .baseUrl(mistralApiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + mistralApiKey)
                .build();
    }
}