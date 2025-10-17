package com.rahul.genmillenauts.userservice.dto;

import lombok.Data;

@Data
public class UserResponse {
    private String email;   // optional
    private String mobile;  // optional
    private String anyName;
    private String role;
    private String fullName;
}
