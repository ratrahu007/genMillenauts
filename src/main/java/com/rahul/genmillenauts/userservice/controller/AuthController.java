package com.rahul.genmillenauts.userservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rahul.genmillenauts.global.service.JwtService;
import com.rahul.genmillenauts.userservice.dto.LoginRequest;
import com.rahul.genmillenauts.userservice.dto.LoginResponse;
import com.rahul.genmillenauts.userservice.dto.OtpResponse;
import com.rahul.genmillenauts.userservice.dto.RegisterRequest;
import com.rahul.genmillenauts.userservice.dto.SendOtpRequest;
import com.rahul.genmillenauts.userservice.dto.UserResponse;
import com.rahul.genmillenauts.userservice.dto.VerifyOtpRequest;
import com.rahul.genmillenauts.userservice.entity.User;
import com.rahul.genmillenauts.userservice.userserviceinner.Userservice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final Userservice userService;
    private final JwtService jwtService;   // ✅ Inject JwtService
    private final Logger log = LoggerFactory.getLogger(AuthController.class);

 // src/main/java/com/rahul/genmillenauts/controller/AuthController.java
    @PostMapping("/send-otp")
    public ResponseEntity<OtpResponse> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        log.info("Received send-otp request: {}", request);

        try {
            // Call service to actually send OTP (email only)
            userService.sendOtp(request.getEmail(), null);

            // Return JSON response
            return ResponseEntity.ok(new OtpResponse(true, "OTP sent successfully"));
        } catch (Exception e) {
            log.error("Error sending OTP: ", e);

            // Return JSON even on failure
            return ResponseEntity.ok(new OtpResponse(false, "Failed to send OTP"));
        }
    }

   
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("Received verify-otp request: {}", request);

        String result = userService.verifyOtp(request.getEmail(), request.getMobile(), request.getOtp());

        Map<String, Object> response = new HashMap<>();
        response.put("message", result);

        // if string contains "successfully", treat it as success
        boolean success = result.toLowerCase().contains("success");
        response.put("success", success);

        if (success) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received register request: {}", request);
        try {
            User user = User.builder()
                    .email(request.getEmail())
                    .mobile(request.getMobile())
                    .password(request.getPassword())
                    .anyName(request.getAnyName())
                    .role(request.getRole())
                    .fullName(request.getFullName())
                    .city(request.getCity())
                    .build();

            User savedUser = userService.registerUser(user);

            UserResponse response = new UserResponse();
            response.setEmail(savedUser.getEmail());
            response.setMobile(savedUser.getMobile());
            response.setAnyName(savedUser.getAnyName());
            response.setRole(savedUser.getRole());
            response.setFullName(savedUser.getFullName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error registering user: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request: {}", request);
        try {
            LoginResponse response = userService.login(request.getEmail(), request.getMobile(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error logging in user: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


   
}
