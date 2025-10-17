package com.rahul.genmillenauts.userservice.controller;

import com.rahul.genmillenauts.global.service.JwtService;
import com.rahul.genmillenauts.userservice.dto.*;
import com.rahul.genmillenauts.userservice.entity.User;
import com.rahul.genmillenauts.userservice.userserviceinner.Userservice;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final Userservice userService;
    private final JwtService jwtService;   // ✅ Inject JwtService
    private final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        log.info("Received send-otp request: {}", request);
        try {
            userService.sendOtp(request.getEmail(), request.getMobile());
            return ResponseEntity.ok("OTP sent successfully");
        } catch (Exception e) {
            log.error("Error sending OTP: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("Received verify-otp request: {}", request);
        String result = userService.verifyOtp(request.getEmail(), request.getMobile(), request.getOtp());

        if (result.contains("successfully")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
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
