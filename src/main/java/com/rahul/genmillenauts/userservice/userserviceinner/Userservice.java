package com.rahul.genmillenauts.userservice.userserviceinner;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.rahul.genmillenauts.global.config.CustomUserDetails;
import com.rahul.genmillenauts.global.service.JwtService;
import com.rahul.genmillenauts.global.service.MessageService;
import com.rahul.genmillenauts.userservice.dto.LoginResponse;
import com.rahul.genmillenauts.userservice.dto.UserFullResponse;
import com.rahul.genmillenauts.userservice.dto.UserPublicResponse;
import com.rahul.genmillenauts.userservice.dto.UserUpdateDto;
import com.rahul.genmillenauts.userservice.entity.OtpData;
import com.rahul.genmillenauts.userservice.entity.User;
import com.rahul.genmillenauts.userservice.repository.OtpRepository;
import com.rahul.genmillenauts.userservice.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Userservice {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final MessageService messageService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService;
    private final Logger log = LoggerFactory.getLogger(Userservice.class);

    // ---------------------- Send OTP ----------------------
    public void sendOtp(String email, String mobile) throws Exception {
        if ((email == null || email.isEmpty()) && (mobile == null || mobile.isEmpty())) {
            throw new Exception("Email or Mobile must be provided");
        }

        if (mobile != null) mobile = normalizeMobile(mobile);

        String otp = String.format("%06d", new Random().nextInt(999999));
        OtpData otpData = OtpData.builder()
                .otp(otp)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .email(email)
                .mobile(mobile)
                .build();

        if (email != null) messageService.sendEmail(email, "Your OTP", "OTP: " + otp);
        if (mobile != null) messageService.sendOtpSms(mobile, "OTP: " + otp);

        otpRepository.save(otpData);
        log.info("OTP sent successfully: {}", otp);
    }

    // ---------------------- Verify OTP ----------------------
 // ---------------------- Verify OTP ----------------------
    
    public String verifyOtp(String email, String mobile, String otp) {
        // Normalize mobile number if provided
        if (mobile != null) mobile = normalizeMobile(mobile);

        Optional<OtpData> otpOptional = Optional.empty();

        // First check email OTP if provided
        if (email != null) {
            otpOptional = otpRepository.findTopByEmailAndStatusOrderByCreatedAtDesc(email, "PENDING");
        }

        // If no email OTP found, check mobile OTP
        if (otpOptional.isEmpty() && mobile != null) {
            otpOptional = otpRepository.findTopByMobileAndStatusOrderByCreatedAtDesc(mobile, "PENDING");
        }

        if (otpOptional.isEmpty()) {
            log.info("No OTP found for verification. email={} mobile={}", email, mobile);
            return "No OTP request found or already verified.";
        }

        OtpData otpData = otpOptional.get();
        log.info("Found OTP in DB: {}", otpData.getOtp());

        // Check expiration
        if (otpData.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpData.setStatus("EXPIRED");
            otpRepository.save(otpData);
            return "OTP expired. Please request a new one.";
        }

        // Verify OTP
        if (otpData.getOtp().equals(otp)) {
            otpData.setStatus("VERIFIED");
            otpRepository.save(otpData);

            // Cleanup old OTPs
            if (email != null) otpRepository.deleteAllByEmailAndStatus(email, "PENDING");
            if (mobile != null) otpRepository.deleteAllByMobileAndStatus(mobile, "PENDING");

            return "OTP verified successfully.";
        }

        return "Invalid OTP. Please try again.";
    }

    // ---------------------- Helper: normalize mobile ----------------------
    private String normalizeMobile(String mobile) {
        mobile = mobile.replaceAll("[\\s\\-]", "");
        if (!mobile.startsWith("+91")) {
            if (mobile.startsWith("91")) mobile = "+" + mobile;
            else if (mobile.length() == 10) mobile = "+91" + mobile;
        }
        return mobile;
    }

    // ---------------------- Register User ----------------------
    public User registerUser(User user) throws Exception {
        Optional<OtpData> otpOptional = user.getEmail() != null ?
                otpRepository.findByEmailAndStatus(user.getEmail(), "VERIFIED") :
                otpRepository.findByMobileAndStatus(user.getMobile(), "VERIFIED");

        if (otpOptional.isEmpty()) throw new Exception("OTP not verified");

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        OtpData otpData = otpOptional.get();
        otpData.setUser(user);
        user.setOtpData(otpData);

        return userRepository.save(user);
    }
    
    
    
//login
    public LoginResponse login(String email, String mobile, String password) throws Exception {
        Optional<User> userOptional = email != null ?
                userRepository.findByEmail(email) :
                userRepository.findByMobile(mobile);

        if (userOptional.isEmpty()) throw new Exception("User not found");

        User user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Invalid password");
        }

        // ✅ Create CustomUserDetails
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // ✅ Generate token with userId included
        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(token, user.getEmail(), user.getRole());
    }

    
    //get a profile of logged in user
    public UserFullResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        System.out.println("Fetched user: " + user);
        return UserFullResponse.fromEntity(user);
    }

    
    public UserPublicResponse getUserPublicProfile(String anyName) {
    	User user =userRepository.findByAnyName(anyName)
    			.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"User Mot Found"));
    	return UserPublicResponse.fromEntity(user);
    }
    
    
    //Update a user With city and isOptedInForOfflineMeets
    
    public UserUpdateDto updateUserProfile(Long userId, UserUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));

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

