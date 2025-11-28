package com.rahul.genmillenauts.aiservice.controller;

import com.rahul.genmillenauts.aiservice.dto.LatestMoodResponse;
import com.rahul.genmillenauts.aiservice.dto.WeeklyMoodResponse;
import com.rahul.genmillenauts.aiservice.entity.DailyMood;
import com.rahul.genmillenauts.aiservice.entity.StressLog;
import com.rahul.genmillenauts.aiservice.repository.DailyMoodRepository;
import com.rahul.genmillenauts.aiservice.repository.StressLogRepository;
import com.rahul.genmillenauts.aiservice.service.StressAnalysisService;
import com.rahul.genmillenauts.userservice.entity.User;
import com.rahul.genmillenauts.userservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ai/stress")
@RequiredArgsConstructor
public class StressController {

    private final StressAnalysisService stressService;
    private final UserRepository userRepo;
    private final StressLogRepository stressLogRepo;
    private final DailyMoodRepository dailyMoodRepo;

    // ---------------------------------------------------------
    // 1) ASYNC STRESS ANALYSIS (already existed)
    // ---------------------------------------------------------
    @GetMapping("/analyze")
    public ResponseEntity<String> analyze(@RequestAttribute("userId") Long userId) {

        stressService.analyzeAsync(userId);

        return ResponseEntity.accepted().body("Stress analysis started...");
    }

    // ---------------------------------------------------------
    // 2) LATEST MOOD (real-time dashboard card)
    // ---------------------------------------------------------
    @GetMapping("/latest")
    public LatestMoodResponse getLatestMood(@RequestAttribute("userId") Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StressLog latest = stressLogRepo.findFirstByUserOrderByCreatedAtDesc(user);

        return new LatestMoodResponse(
                latest.getStressIndex(),
                latest.getMood(),
                latest.getCreatedAt()
        );
    }

    // ---------------------------------------------------------
    // 3) WEEKLY MOOD (chart data)
    // ---------------------------------------------------------
    @GetMapping("/weekly")
    public List<WeeklyMoodResponse> getWeeklyMood(@RequestAttribute("userId") Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(6);

        List<DailyMood> moods = dailyMoodRepo.findByUserAndDateBetween(user, start, today);

        return moods.stream()
                .map(m -> new WeeklyMoodResponse(
                        m.getDate(),
                        m.getAverageStress(),
                        m.getOverallMood()
                ))
                .toList();
    }
}
