package com.rahul.genmillenauts.global.dto;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor

@Builder
@Data
public class OtpResponse {


    private boolean success;
    private String message;

    public OtpResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    // getters and setters
}