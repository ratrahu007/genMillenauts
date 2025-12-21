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

    // load GCP credentials from Railway ENV instead of system/file
    private GoogleCredentials loadCredentials() {
        try {
            // read full JSON from environment variable
            String json = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON");

            // fail fast if ENV is missing
            if (json == null || json.isBlank()) {
                throw new IllegalStateException("GOOGLE_APPLICATION_CREDENTIALS_JSON not set");
            }

            // create credentials in-memory (no file dependency)
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

        // fetch user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        String userMessage = request.getMessage();

        try {
            // explicitly load credentials from ENV
            GoogleCredentials credentials = loadCredentials();

            // build VertexAI client with explicit credentials
            try (VertexAI vertexAI = new VertexAI.Builder()
                    .setProjectId(PROJECT_ID)
                    .setLocation(LOCATION)
                    .setCredentials(credentials)
                    .build()) {

                GenerativeModel model = new GenerativeModel(MODEL_NAME, vertexAI);

                GenerateContentResponse response = model.generateContent(userMessage);

                String bot = response.getCandidates(0)
                        .getContent()
                        .getParts(0)
                        .getText();

                // persist user + bot messages
                chatRepo.saveAll(List.of(
                        new ChatMessage(null, user, userMessage, false, LocalDateTime.now()),
                        new ChatMessage(null, user, bot, true, LocalDateTime.now())
                ));

                log.info("💬 Bot reply generated successfully");
                return new ChatResponse(bot);
            }

        } catch (Exception e) {
            log.error("❌ Vertex AI error", e);
            return new ChatResponse("AI is busy right now. Please try again.");
        }
    }
}
