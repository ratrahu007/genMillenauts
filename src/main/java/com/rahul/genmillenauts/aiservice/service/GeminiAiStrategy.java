package com.rahul.genmillenauts.aiservice.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Slf4j

@Primary
@Component("gemini")
public class GeminiAiStrategy implements AiStrategy {

    private static final String PROJECT_ID = "stress1mgmt";
    private static final String LOCATION = "us-central1";
    private static final String MODEL_NAME = "gemini-2.5-flash";

    @Override
    public String generateContent(String prompt) {
        try {
            GoogleCredentials credentials = loadCredentials();
            try (VertexAI vertexAI = new VertexAI(PROJECT_ID, LOCATION, credentials)) {
                GenerativeModel model = new GenerativeModel(MODEL_NAME, vertexAI);
                GenerateContentResponse response = model.generateContent(prompt);
                return response.getCandidates(0)
                        .getContent()
                        .getParts(0)
                        .getText();
            }
        } catch (Exception e) {
            log.error("Gemini AI generation failed", e);
            throw new RuntimeException("AI generation failed", e);
        }
    }

    private GoogleCredentials loadCredentials() {
        try {
            String json = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON");
            if (json == null || json.isBlank()) {
                throw new IllegalStateException("GOOGLE_APPLICATION_CREDENTIALS_JSON not set");
            }
            return GoogleCredentials.fromStream(
                    new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))
            );
        } catch (Exception e) {
            log.error("Failed to load GCP credentials from ENV", e);
            throw new RuntimeException("GCP authentication failed");
        }
    }
}
