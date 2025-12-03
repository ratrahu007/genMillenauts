package com.rahul.genmillenauts.therapist.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TherapistProfileResponse {
    private Long id;
    private String fullName;
    private String email;
    private String mobile;
    private String specialization;
    private String city;
    private String bio;
    private Double sessionPrice;
    private String profilePhotoUrl;
}
