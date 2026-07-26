package com.rahul.genmillenauts.aiservice.service;

import com.rahul.genmillenauts.aiservice.dto.GeminiRequest;
import com.rahul.genmillenauts.aiservice.dto.GeminiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;


@Primary
@Slf4j
@Component("gemini")
public class GeminiAiStrategy implements AiStrategy {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.model}")
    private String model;

    private final RestClient restClient = RestClient.create();

    @Override
    public String generateContent(String prompt) {

        GeminiRequest request = new GeminiRequest(
                List.of(
                        new GeminiRequest.Content(
                                List.of(
                                        new GeminiRequest.Part(prompt)
                                )
                        )
                )
        );

        String url = apiUrl + "/" + model + ":generateContent?key=" + apiKey;

        try {

            GeminiResponse response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(GeminiResponse.class);

            if (response == null
                    || response.getCandidates() == null
                    || response.getCandidates().isEmpty()
                    || response.getCandidates().get(0).getContent() == null
                    || response.getCandidates().get(0).getContent().getParts() == null
                    || response.getCandidates().get(0).getContent().getParts().isEmpty()) {

                throw new RuntimeException("No response received from Gemini.");
            }

            return response.getCandidates()
                    .get(0)
                    .getContent()
                    .getParts()
                    .get(0)
                    .getText();

        } catch (Exception e) {
            log.error("Error while calling Gemini API", e);
            throw new RuntimeException("Failed to generate AI response.", e);
        }
    }
}