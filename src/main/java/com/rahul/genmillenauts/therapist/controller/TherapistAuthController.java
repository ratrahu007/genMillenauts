package com.rahul.genmillenauts.therapist.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rahul.genmillenauts.global.dto.ApiResponse;
import com.rahul.genmillenauts.global.dto.OtpResponse;
import com.rahul.genmillenauts.global.dto.SendOtpRequest;
import com.rahul.genmillenauts.global.service.OtpService;
import com.rahul.genmillenauts.therapist.dto.OtpVerifyRequest;
import com.rahul.genmillenauts.therapist.dto.TherapistLoginRequest;
import com.rahul.genmillenauts.therapist.dto.TherapistLoginResponse;
import com.rahul.genmillenauts.therapist.dto.TherapistRegisterRequest;
import com.rahul.genmillenauts.therapist.service.TherapsitAuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/therapists")
@RequiredArgsConstructor
public class TherapistAuthController {

    private final TherapsitAuthService authService;
    private final OtpService otpService;

    // --------------------- SEND OTP ---------------------
    @PostMapping("/send-otp")
    public ResponseEntity<OtpResponse> sendOtp(@Valid @RequestBody SendOtpRequest req) {

        if ((req.getEmail() == null || req.getEmail().isEmpty()) &&
            (req.getMobile() == null || req.getMobile().isEmpty())) {
            return ResponseEntity
                    .badRequest()
                    .body(new OtpResponse(false, "Either email or mobile must be provided"));
        }

        try {
            otpService.sendOtp(req.getEmail(), req.getMobile());
            return ResponseEntity.ok(new OtpResponse(true, "OTP sent successfully"));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(new OtpResponse(false, e.getMessage()));
        }
    }

    // --------------------- VERIFY OTP ---------------------
    @PostMapping("/verify-otp")
    public ResponseEntity<OtpResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest req) {

        try {
            authService.verifyOtpAndMark(req.getEmailOrMobile(), req.getOtp());
            return ResponseEntity.ok(new OtpResponse(true, "OTP verified successfully"));
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new OtpResponse(false, e.getMessage()));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerTherapist(
            @Valid @RequestBody TherapistRegisterRequest req) {

        try {
            authService.registerTherapist(req);
            return ResponseEntity.ok(new ApiResponse(true, "Therapist registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    
    @PostMapping("/login")
    public ResponseEntity<TherapistLoginResponse> login(
            @RequestBody TherapistLoginRequest req) {

        try {
            TherapistLoginResponse response =
                    authService.loginTherapist(req.getEmailOrMobile(), req.getPassword());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new TherapistLoginResponse(false, null, e.getMessage(),null));
        }
    }


}
