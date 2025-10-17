package com.rahul.genmillenauts.userservice.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;   // optional
    private String mobile;  // optional
    private String password;
}
