package com.rahul.genmillenauts.aiservice.service;

import com.rahul.genmillenauts.aiservice.dto.OpenAiRequest;
import com.rahul.genmillenauts.aiservice.dto.OpenAiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component("openai")
public class OpenAiAiStrategy implements AiStrategy {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model;

    private final WebClient.Builder webClientBuilder;

    public OpenAiAiStrategy(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public String generateContent(String prompt) {

        log.info("Generating content using OpenAI model: {}", model);

        OpenAiRequest request = new OpenAiRequest(
                model,
                List.of(
                        new OpenAiRequest.Message(
                                "user",
                                prompt
                        )
                )
        );

        try {

            WebClient webClient = webClientBuilder
                    .baseUrl(apiUrl)
                    .build();

            OpenAiResponse response = webClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OpenAiResponse.class)
                    .block();

            if (response == null
                    || response.choices() == null
                    || response.choices().isEmpty()
                    || response.choices().get(0).message() == null
                    || response.choices().get(0).message().content() == null) {

                throw new RuntimeException("No response received from OpenAI.");
            }

            return response.choices()
                    .get(0)
                    .message()
                    .content();

        } catch (Exception e) {
            log.error("Error while calling OpenAI API", e);
            throw new RuntimeException("Failed to generate AI response.", e);
        }
    }
}