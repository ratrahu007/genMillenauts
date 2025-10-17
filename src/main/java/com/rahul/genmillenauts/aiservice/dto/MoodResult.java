package com.rahul.genmillenauts.aiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoodResult {
    private String mood;
    private double confidence;

 
    // getters & setters
}
