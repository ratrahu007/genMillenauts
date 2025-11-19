package com.rahul.genmillenauts.global.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SendOtpRequest {
    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 10, max = 15, message = "Mobile number must be 10-15 digits")
    private String mobile;
}
