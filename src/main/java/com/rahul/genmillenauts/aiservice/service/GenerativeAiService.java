package com.rahul.genmillenauts.aiservice.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
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

@Service
@RequiredArgsConstructor
public class GenerativeAiService {

    private static final Logger log = LoggerFactory.getLogger(GenerativeAiService.class);

    private final ChatMessageRepository chatRepo;
    private final UserRepository userRepo;

    private static final String PROJECT_ID = "stress1mgmt";     
    private static final String LOCATION = "asia-south1";           
    private static final String MODEL_NAME = "gemini-pro";    

    public ChatResponse getBotReply(ChatRequest chatRequest, Long userId) {
        log.info("🟢 Received chat request from userId: {}", userId);

        // 1️⃣ Find user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        String userMessage = chatRequest.getMessage();
        log.debug("User message: {}", userMessage);

        // 2️⃣ Verify Google Credentials before API call
        try {
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
            log.info("✅ Google Application Default Credentials loaded successfully.");

            if (credentials instanceof ServiceAccountCredentials sac) {
                log.info("👤 Using Service Account: {}", sac.getClientEmail());
            } else {
                log.warn("⚠️ Loaded credentials are not service account type.");
            }

        } catch (IOException e) {
            log.error("❌ Failed to load Google credentials: {}", e.getMessage(), e);
            return new ChatResponse("Sorry, credentials could not be verified. Please check your setup.");
        }

        // 3️⃣ Initialize variables
        String botReply = null;

        try (VertexAI vertexAI = new VertexAI(PROJECT_ID, LOCATION)) {
            log.info("✅ Connected to Vertex AI region: {} | model: {}", LOCATION, MODEL_NAME);

            GenerativeModel model = new GenerativeModel(MODEL_NAME, vertexAI);

            // 4️⃣ Generate bot response
            GenerateContentResponse response = model.generateContent(userMessage);

            if (response == null || response.getCandidatesCount() == 0) {
                log.warn("⚠️ No response candidates received from Gemini.");
                return new ChatResponse("Sorry, I couldn’t generate a response right now.");
            }

            botReply = response.getCandidates(0).getContent().getParts(0).getText();
            log.info("✅ Gemini responded successfully. Response length: {} chars", botReply.length());
            log.debug("Full Gemini response: {}", botReply);

            // 5️⃣ Save both messages
            ChatMessage userMsg = new ChatMessage(null, user, userMessage, false, LocalDateTime.now());
            ChatMessage botMsg = new ChatMessage(null, user, botReply, true, LocalDateTime.now());

            chatRepo.saveAll(List.of(userMsg, botMsg));
            log.info("💾 Messages saved successfully for userId: {}", userId);

            return new ChatResponse(botReply);

        } catch (Exception e) {
            log.error("❌ Error while calling Gemini API: {}", e.getMessage(), e);
            return new ChatResponse("Sorry, I’m having trouble right now. Please try again later.");
        } finally {
            log.info("🟣 [GenAI] Request completed for userId: {}", userId);
        }
    }
}
