package com.rahul.genmillenauts.userservice.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rahul.genmillenauts.userservice.dto.LoginRequest;
import com.rahul.genmillenauts.userservice.dto.LoginResponse;
import com.rahul.genmillenauts.userservice.dto.RegisterRequest;
import com.rahul.genmillenauts.userservice.dto.UserResponse;
import com.rahul.genmillenauts.userservice.entity.User;
import com.rahul.genmillenauts.userservice.userserviceinner.Userservice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final Userservice userService;

    // ✅ REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request received");

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
    }


    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login Request received");

        LoginResponse response = userService.login(
                request.getEmail(),
                request.getMobile(),
                request.getPassword()
        );

        log.info("✅ User logged in successfully:");
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Login successful",
                "token", response.getToken(),
                "email", response.getEmail(),
                "role", response.getRole()
        ));
    }
}
