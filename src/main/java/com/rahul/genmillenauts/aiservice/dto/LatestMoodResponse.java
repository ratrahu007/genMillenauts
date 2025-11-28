package com.rahul.genmillenauts.aiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LatestMoodResponse {
    private int stressIndex;
    private String mood;
    private LocalDateTime time;
}
