package com.rahul.genmillenauts.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 10, max = 15, message = "Mobile number must be 10-15 digits")
    private String mobile;

    @NotBlank(message = "Password must not be blank")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String anyName;
    private String role;
    
    private String city;
}
