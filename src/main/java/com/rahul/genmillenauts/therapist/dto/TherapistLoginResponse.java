package com.rahul.genmillenauts.therapist.dto;

import com.rahul.genmillenauts.global.config.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TherapistLoginResponse {
    private boolean success;
    private String token;
    private String message;
    private Role role;
}
