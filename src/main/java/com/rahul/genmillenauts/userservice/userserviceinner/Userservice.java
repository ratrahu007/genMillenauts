package com.rahul.genmillenauts.userservice.userserviceinner;

import java.util.Optional;

import org.checkerframework.common.returnsreceiver.qual.This;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.rahul.genmillenauts.global.config.CustomUserDetails;
import com.rahul.genmillenauts.global.service.JwtService;
import com.rahul.genmillenauts.global.service.MessageService;
import com.rahul.genmillenauts.global.service.OtpService;
import com.rahul.genmillenauts.userservice.dto.LoginResponse;
import com.rahul.genmillenauts.userservice.dto.UserFullResponse;
import com.rahul.genmillenauts.userservice.dto.UserPublicResponse;
import com.rahul.genmillenauts.userservice.dto.UserUpdateDto;
import com.rahul.genmillenauts.userservice.entity.User;
import com.rahul.genmillenauts.userservice.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Userservice {

    private final UserRepository userRepository;
    private final OtpService otpService; // ✅ use global OTP helper
    private final MessageService messageService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService;
    private final Logger log = LoggerFactory.getLogger(Userservice.class);

    // ---------------------- Register User ----------------------
    public User registerUser(User user) {
        // ✅ Check OTP verified
        boolean verified = otpService.isVerified(user.getEmail(), user.getMobile());
        if (!verified) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Please verify your email or mobile before registration.");
        }

        // ✅ Check for duplicates
        if (user.getEmail() != null && userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "User with this email is already registered.");
        }

        if (user.getMobile() != null && userRepository.findByMobile(user.getMobile()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "User with this mobile number is already registered.");
        }

        // ✅ Encrypt and save
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public LoginResponse login(String email, String mobile, String password) {
        Optional<User> userOptional = email != null ?
                userRepository.findByEmail(email) :
                userRepository.findByMobile(mobile);

        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        // ✅ Generate token
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(token, user.getEmail(), user.getRole());
    }
    
    // ---------------------- Get Logged-in User Profile ----------------------
    public UserFullResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return UserFullResponse.fromEntity(user);
    }

    // ---------------------- Get Public Profile by anyName ----------------------
    public UserPublicResponse getUserPublicProfile(String anyName) {
        User user = userRepository.findByAnyName(anyName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return UserPublicResponse.fromEntity(user);
    }

    // ---------------------- Update Profile ----------------------
    @Transactional
    public UserUpdateDto updateUserProfile(Long userId, UserUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (dto.getCity() != null) {
            user.setCity(dto.getCity());
        }

        if (dto.getIsOptedForOfflineMeets() != null) {
            user.setOptedForOnlineMeets(dto.getIsOptedForOfflineMeets());
        }

        User saved = userRepository.save(user);

        UserUpdateDto result = new UserUpdateDto();
        result.setCity(saved.getCity());
        result.setIsOptedForOfflineMeets(saved.isOptedForOnlineMeets());

        return result;
    }
}
