// dto/AlertContactDto.java
package com.rahul.genmillenauts.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertContactDto {
    private Long id; // for response
    @NotBlank
    private String name;
    @NotBlank
    private String phone;
    private String relation;
}
