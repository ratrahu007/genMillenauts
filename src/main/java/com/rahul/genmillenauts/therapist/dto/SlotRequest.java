package com.rahul.genmillenauts.therapist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SlotRequest {
    private String dayOfWeek;       // "MONDAY"
    private String startTime;       // "10:00"
    private String endTime;         // "13:00"
    private int durationMinutes;    // 45

    // Getters and Setters
}
