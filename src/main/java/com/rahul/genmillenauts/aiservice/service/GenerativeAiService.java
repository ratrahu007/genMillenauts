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

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerativeAiService {

    private final ChatMessageRepository chatRepo;
    private final UserRepository userRepo;

    private static final String PROJECT_ID = "stress1mgmt";

    // ⭐ Guaranteed working region
    private static final String LOCATION = "us-central1";

    // ⭐ Guaranteed working model
    private static final String MODEL_NAME = "gemini-2.5-flash";

    public ChatResponse getBotReply(ChatRequest request, Long userId) {
        log.info("🟢 Request received from user {}", userId);

        // find user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        String userMessage = request.getMessage();

        // Load GCP credentials
        try {
            GoogleCredentials.getApplicationDefault();
            log.info("✅ Google credentials loaded");
        } catch (Exception e) {
            log.error("❌ Failed to load Google ADC", e);
            return new ChatResponse("Server authentication issue. Please try later.");
        }

        try (VertexAI vertexAI = new VertexAI(PROJECT_ID, LOCATION)) {

            GenerativeModel model = new GenerativeModel(MODEL_NAME, vertexAI);

            GenerateContentResponse response = model.generateContent(userMessage);

            String bot = response.getCandidates(0)
                    .getContent()
                    .getParts(0)
                    .getText();

            // SAVE chat messages
            chatRepo.saveAll(List.of(
            		new ChatMessage(null, user, userMessage, false, LocalDateTime.now()),
            	    new ChatMessage(null, user, bot, true, LocalDateTime.now())
            ));

            log.info("💬 Bot reply generated successfully");
            return new ChatResponse(bot);

        } catch (Exception e) {
            log.error("❌ Vertex AI error", e);
            return new ChatResponse("AI is busy right now. Please try again.");
        }
    }
}
