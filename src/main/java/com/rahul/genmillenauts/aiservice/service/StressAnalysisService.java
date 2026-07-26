package com.rahul.genmillenauts.aiservice.service;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    private final Map<String, AiStrategy> aiStrategies;

    @Value("${ai.provider}")
    private String provider;

    private static final int ALERT_THRESHOLD = 80;

    @Async("stressExecutor")
    public void analyzeAsync(Long userId) {

        try {

            log.info("Async stress analysis started for user {}", userId);

            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<com.rahul.genmillenauts.aiservice.entity.ChatMessage> last10 =
                    chatRepo.findTop10ByUserOrderByCreatedAtDesc(user);

            if (last10.isEmpty()) {
                log.warn("No chat history found for user {}", userId);
                return;
            }

            String combined = last10.stream()
                    .map(com.rahul.genmillenauts.aiservice.entity.ChatMessage::getMessage)
                    .reduce("", (a, b) -> a + "\n" + b);

            int stress = generateStressScore(combined);

            String mood = classifyMood(stress);

            stressRepo.save(
                    StressLog.builder()
                            .user(user)
                            .stressIndex(stress)
                            .mood(mood)
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            updateDailyMood(user, stress);

            if (stress >= ALERT_THRESHOLD) {
                sendAlerts(user, stress, mood);
            }

            log.info("Stress analysis completed for user {} | Stress={}", userId, stress);

        } catch (Exception e) {
            log.error("Async stress analysis failed for user {}", userId, e);
        }
    }

    private int generateStressScore(String text) {

        try {

            String prompt = """
                    Analyze the emotional stress level of the conversation below.

                    Return ONLY a number between 0 and 100.

                    Messages:
                    """ + text;

            AiStrategy strategy = aiStrategies.get(provider);

            if (strategy == null) {
                throw new RuntimeException("AI Provider not found: " + provider);
            }

            String output = strategy.generateContent(prompt);

            String digits = output.replaceAll("[^0-9]", "");

            if (digits.isEmpty()) {
                return 50;
            }

            int value = Integer.parseInt(digits);

            return Math.max(0, Math.min(100, value));

        } catch (Exception e) {

            log.error("Stress scoring failed", e);

            return 50;
        }
    }

    private String classifyMood(int stress) {

        if (stress < 30) return "HAPPY";
        if (stress < 60) return "NEUTRAL";
        if (stress < 80) return "STRESSED";

        return "CRISIS";
    }

    private void updateDailyMood(User user, int stress) {

        LocalDate today = LocalDate.now();

        DailyMood dm = moodRepo.findByUserAndDate(user, today)
                .orElse(null);

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

    private void sendAlerts(User user, int stress, String mood) {

        List<AlertContact> contacts = alertRepo.findByUserId(user.getId());

        String body = """
                URGENT ALERT:
                %s is showing signs of emotional distress.
                Stress Index: %d
                Mood: %s
                Please check on them immediately.
                """.formatted(user.getFullName(), stress, mood);

        for (AlertContact contact : contacts) {

            try {

                messageService.sendAlertSms(contact.getPhone(), body);

            } catch (Exception e) {

                log.error("Failed to send alert to {}", contact.getPhone(), e);
            }
        }
    }
}