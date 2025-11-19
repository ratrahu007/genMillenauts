package com.rahul.genmillenauts.therapist.dto;

import lombok.Data;

@Data
public class TherapistRegisterRequest {
    private String fullName;
    private String email;
    private String mobile;
    private String password;
    private String specialization;
    private String city;
    private double sessionPrice;
    private String bio;
    private String role;
}

