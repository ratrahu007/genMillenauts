package com.rahul.genmillenauts.aiservice.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.rahul.genmillenauts.aiservice.entity.DailyMood;
import com.rahul.genmillenauts.aiservice.entity.StressLog;
import com.rahul.genmillenauts.aiservice.repository.ChatMessageRepository;
import com.rahul.genmillenauts.aiservice.repository.DailyMoodRepository;
import com.rahul.genmillenauts.aiservice.repository.StressLogRepository;
import com.rahul.genmillenauts.global.service.MessageService;
import com.rahul.genmillenauts.userservice.entity.AlertContact;
import com.rahul.genmillenauts.userservice.entity.User;
import com.rahul.genmillenauts.userservice.repository.AlertContactRepository;
import com.rahul.genmillenauts.userservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/*
   FINAL VERSION:
   ✔ FULLY ASYNC stress analysis
   ✔ Does not block main chat thread
   ✔ Runs in background using stressExecutor
   ✔ SAFE for Railway / Render (ENV-based GCP auth)
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class StressAnalysisService {

    private final UserRepository userRepo;
    private final ChatMessageRepository chatRepo;
    private final StressLogRepository stressRepo;
    private final DailyMoodRepository moodRepo;
    private final AlertContactRepository alertRepo;
    private final MessageService messageService;

    private static final String PROJECT_ID = "stress1mgmt";
    private static final String LOCATION = "us-central1";
    private static final String MODEL_NAME = "gemini-2.5-flash";

    private static final int ALERT_THRESHOLD = 80;

    // 🔐 Load GCP credentials from ENV (Railway / Render safe)
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

    // 🚀 ASYNC ENTRY POINT
    @Async("stressExecutor")
    public void analyzeAsync(Long userId) {

        try {
            log.info("🚀 Async Stress Analysis Launched for user {}", userId);

            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            List<com.rahul.genmillenauts.aiservice.entity.ChatMessage> last10 =
                    chatRepo.findTop10ByUserOrderByCreatedAtDesc(user);

            if (last10.isEmpty()) {
                log.warn("No chats found for user {}", userId);
                return;
            }

            String combined = last10.stream()
                    .map(com.rahul.genmillenauts.aiservice.entity.ChatMessage::getMessage)
                    .reduce("", (a, b) -> a + "\n" + b);

            int stress = generateStressScore(combined);
            String mood = classifyMood(stress);

            // Save stress log
            stressRepo.save(
                    StressLog.builder()
                            .user(user)
                            .stressIndex(stress)
                            .mood(mood)
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            // Save daily mood
            updateDailyMood(user, stress);

            // Send alerts if needed
            if (stress >= ALERT_THRESHOLD) {
                sendAlerts(user, stress, mood);
            }

            log.info("✅ Async stress analysis completed for user {} | Index={}", userId, stress);

        } catch (Exception e) {
            log.error("❌ Async stress analysis failed for user {} | {}", userId, e.getMessage());
        }
    }

    // ⭐ Generate stress score using Vertex AI (Gemini)
    private int generateStressScore(String text) {

        GoogleCredentials credentials = loadCredentials();

        try (VertexAI vertexAI = new VertexAI.Builder()
                .setProjectId(PROJECT_ID)
                .setLocation(LOCATION)
                .setCredentials(credentials)
                .build()) {

            GenerativeModel model = new GenerativeModel(MODEL_NAME, vertexAI);

            String prompt = """
                Analyze the emotional stress level of the conversation below.
                Return ONLY a number (0-100).

                Messages:
                """ + text;

            GenerateContentResponse response = model.generateContent(prompt);

            String output = response.getCandidates(0)
                    .getContent()
                    .getParts(0)
                    .getText();

            log.info("🔍 LLM RAW OUTPUT => {}", output);

            String digits = output.replaceAll("[^0-9]", "");
            if (digits.isEmpty()) return 50;

            int val = Integer.parseInt(digits);
            return Math.min(100, Math.max(0, val));

        } catch (Exception e) {
            log.error("❌ LLM stress scoring failed: {}", e.getMessage());
            return 50;
        }
    }

    // ⭐ Convert stress number to mood label
    private String classifyMood(int stress) {
        if (stress < 30) return "HAPPY";
        if (stress < 60) return "NEUTRAL";
        if (stress < 80) return "STRESSED";
        return "CRISIS";
    }

    // ⭐ Update daily mood (reset per day)
    private void updateDailyMood(User user, int stress) {

        LocalDate today = LocalDate.now();

        DailyMood dm = moodRepo.findByUserAndDate(user, today).orElse(null);

        if (dm == null) {
            dm = DailyMood.builder()
                    .user(user)
                    .date(today)
                    .averageStress(stress)
                    .overallMood(classifyMood(stress))
                    .build();
        } else {
            dm.setAverageStress(stress);
            dm.setOverallMood(classifyMood(stress));
        }

        moodRepo.save(dm);
    }

    // ⭐ Send alert SMS
    private void sendAlerts(User user, int stress, String mood) {

        List<AlertContact> contacts = alertRepo.findByUserId(user.getId());

        String body = """
            URGENT ALERT:
            %s is showing signs of emotional distress.
            Stress Index: %d
            Mood: %s
            Please check on them immediately.
            """.formatted(user.getFullName(), stress, mood);

        for (AlertContact c : contacts) {
            try {
                messageService.sendAlertSms(c.getPhone(), body);
                log.info("📩 Alert SMS sent to {}", c.getPhone());
            } catch (Exception e) {
                log.error("❌ Failed to send alert to {}: {}", c.getPhone(), e.getMessage());
            }
        }
    }
}
