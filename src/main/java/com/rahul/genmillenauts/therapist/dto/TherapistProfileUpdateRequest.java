package com.rahul.genmillenauts.therapist.dto;

import lombok.Data;

@Data
public class TherapistProfileUpdateRequest {
    private String fullName;
    private String mobile;
    private String specialization;
    private String city;
    private String bio;
    private Double sessionPrice;
    private String profilePhotoUrl; 
}
