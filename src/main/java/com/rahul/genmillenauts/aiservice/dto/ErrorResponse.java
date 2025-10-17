package com.rahul.genmillenauts.aiservice.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
	
	 private String message;
	    private String path;
	    private Instant timestamp = Instant.now();
}
