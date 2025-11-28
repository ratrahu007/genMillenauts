package com.rahul.genmillenauts.aiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class WeeklyMoodResponse {
    private LocalDate date;
    private int averageStress;
    private String overallMood;
}
