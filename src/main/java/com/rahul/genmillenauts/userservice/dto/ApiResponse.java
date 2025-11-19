package com.rahul.genmillenauts.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    public ApiResponse(boolean b, String string) {
		// TODO Auto-generated constructor stub
	}
	private String message;
    private boolean success;
}

