package com.rahul.genmillenauts.aiservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.rahul.genmillenauts.aiservice.dto.ChatRequest;
import com.rahul.genmillenauts.aiservice.dto.ChatResponse;
import com.rahul.genmillenauts.aiservice.entity.ChatMessage;
import com.rahul.genmillenauts.aiservice.repository.ChatMessageRepository;
import com.rahul.genmillenauts.userservice.entity.User;
import com.rahul.genmillenauts.userservice.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GenerativeAiService {

    private final ChatMessageRepository chatRepo;
    private final UserRepository userRepo;
    private final Map<String, AiStrategy> aiStrategies;

    public GenerativeAiService(ChatMessageRepository chatRepo, UserRepository userRepo, Map<String, AiStrategy> aiStrategies) {
        this.chatRepo = chatRepo;
        this.userRepo = userRepo;
        this.aiStrategies = aiStrategies;
    }

    public ChatResponse getBotReply(ChatRequest request, Long userId) {
        log.info("🟢 Request received from user {}", userId);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        String userMessage = request.getMessage();

        try {
            String provider = request.getProvider();
            AiStrategy selectedStrategy = aiStrategies.get(provider != null ? provider : "gemini");

            if (selectedStrategy == null) {
                log.error("❌ Invalid AI provider specified: {}", provider);
                return new ChatResponse("Invalid AI provider specified. Please try again.");
            }

            String botReply = selectedStrategy.generateContent(userMessage);

            chatRepo.saveAll(List.of(
                    new ChatMessage(null, user, userMessage, false, LocalDateTime.now()),
                    new ChatMessage(null, user, botReply, true, LocalDateTime.now())
            ));

            log.info("💬 Bot reply generated successfully using provider: {}", provider);
            return new ChatResponse(botReply);

        } catch (Exception e) {
            log.error("❌ AI service error", e);
            return new ChatResponse("AI is busy right now. Please try again.");
        }
    }
}
