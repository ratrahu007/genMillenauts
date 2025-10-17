package com.rahul.genmillenauts.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserUpdateDto {

	

    private String city;

    private Boolean isOptedForOfflineMeets;
}
