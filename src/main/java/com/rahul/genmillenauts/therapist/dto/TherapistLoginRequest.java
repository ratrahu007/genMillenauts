package com.rahul.genmillenauts.therapist.dto;

import lombok.Data;

@Data
public class TherapistLoginRequest {
    private String emailOrMobile;
    private String password;
}
