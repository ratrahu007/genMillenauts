package com.rahul.genmillenauts.global.controller;

import com.rahul.genmillenauts.global.dto.OtpResponse;
import com.rahul.genmillenauts.global.dto.SendOtpRequest;
import com.rahul.genmillenauts.global.dto.VerifyOtpRequest;
import com.rahul.genmillenauts.global.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;
    private final Logger log = LoggerFactory.getLogger(OtpController.class);

    // ---------------------- Send OTP ----------------------
    @PostMapping("/send-otp")
    public ResponseEntity<OtpResponse> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        log.info("Received send-otp request: {}", request);

        try {
            otpService.sendOtp(request.getEmail(), request.getMobile());
            return ResponseEntity.ok(new OtpResponse(true, "OTP sent successfully"));
        } catch (Exception e) {
            log.error("Error sending OTP: ", e);
            return ResponseEntity.ok(new OtpResponse(false, "Failed to send OTP: " + e.getMessage()));
        }
    }

    // ---------------------- Verify OTP ----------------------
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("Received verify-otp request: {}", request);

        String result = otpService.verifyOtp(request.getEmail(), request.getMobile(), request.getOtp());

        Map<String, Object> response = new HashMap<>();
        response.put("message", result);
        boolean success = result.toLowerCase().contains("success");
        response.put("success", success);

        if (success) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
