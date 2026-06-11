package com.rahul.genmillenauts.aiservice.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.rahul.genmillenauts.aiservice.dto.ChatRequest;
import com.rahul.genmillenauts.aiservice.dto.ChatResponse;
import com.rahul.genmillenauts.aiservice.entity.ChatMessage;
import com.rahul.genmillenauts.aiservice.repository.ChatMessageRepository;
import com.rahul.genmillenauts.userservice.entity.User;
import com.rahul.genmillenauts.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerativeAiService {

    private final ChatMessageRepository chatRepo;
    private final UserRepository userRepo;

    // GCP project id
    private static final String PROJECT_ID = "stress1mgmt";

    // Vertex AI region
    private static final String LOCATION = "us-central1";

    // Gemini model
    private static final String MODEL_NAME = "gemini-2.5-flash";

    /**
     * Loads GCP credentials from environment variable
     * GOOGLE_APPLICATION_CREDENTIALS_JSON
     */
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
            log.error("❌ Failed to load GCP credentials from ENV", e);
            throw new RuntimeException("GCP authentication failed");
        }
    }

    public ChatResponse getBotReply(ChatRequest request, Long userId) {
        log.info("🟢 Request received from user {}", userId);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        String userMessage = request.getMessage();

        try {
            GoogleCredentials credentials = loadCredentials();

            // ✅ CORRECT VertexAI initialization (NO Builder)
            try (VertexAI vertexAI = new VertexAI(PROJECT_ID, LOCATION, credentials)) {

                GenerativeModel model = new GenerativeModel(MODEL_NAME, vertexAI);

                GenerateContentResponse response = model.generateContent(userMessage);

                String botReply = response.getCandidates(0)
                        .getContent()
                        .getParts(0)
                        .getText();

                chatRepo.saveAll(List.of(
                        new ChatMessage(null, user, userMessage, false, LocalDateTime.now()),
                        new ChatMessage(null, user, botReply, true, LocalDateTime.now())
                ));

                log.info("💬 Bot reply generated successfully");
                return new ChatResponse(botReply);
            }

        } catch (Exception e) {
            log.error("❌ Vertex AI error", e);
            return new ChatResponse("AI is busy right now. Please try again.");
        }
    }
}
