package com.rahul.genmillenauts.global.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 10, max = 15, message = "Mobile number must be 10-15 digits")
    private String mobile;

    @NotBlank(message = "OTP must not be blank")
    private String otp;
}
