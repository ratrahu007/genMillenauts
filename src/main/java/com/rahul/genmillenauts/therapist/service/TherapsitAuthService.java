package com.rahul.genmillenauts.therapist.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rahul.genmillenauts.common.exceptions.OtpNotVerifiedException;
import com.rahul.genmillenauts.global.config.Role;
import com.rahul.genmillenauts.global.service.JwtService;
import com.rahul.genmillenauts.global.service.OtpService;
import com.rahul.genmillenauts.therapist.dto.TherapistLoginResponse;
import com.rahul.genmillenauts.therapist.dto.TherapistRegisterRequest;
import com.rahul.genmillenauts.therapist.entity.Therapist;
import com.rahul.genmillenauts.therapist.repository.TherapistRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TherapsitAuthService {
	

    private final TherapistRepository therapistRepository;
    private final OtpService otpService;          // global OTP service (single source of truth)
    private final JwtService jwtService;          // your existing JWT helper
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    private boolean isProbablyEmail(String input) {
        if (input == null) return false;
        return input.contains("@") && input.contains("."); // simple check
    }

    private boolean isProbablyMobile(String input) {
        if (input == null) return false;
        return input.matches("^\\+?\\d{10,15}$"); // Indian 10-digit mobile
    }

    
    
    @Transactional
    public void verifyOtpAndMark(String emailOrMobile, String otp) {

        // 1️⃣ Verify OTP using global OTP service
        String msg = otpService.verifyOtp(
                isProbablyEmail(emailOrMobile) ? emailOrMobile : null,
                isProbablyMobile(emailOrMobile) ? emailOrMobile : null,
                otp
        );

        // 2️⃣ If OTP NOT verified → stop here
        if (!"OTP verified successfully.".equals(msg)) {
            throw new OtpNotVerifiedException(msg);
        }

        // 3️⃣ Delete ALL other OTPs for this email/mobile
        if (isProbablyEmail(emailOrMobile)) {
            otpService.deleteAllExceptVerified(emailOrMobile, null);
        } else if (isProbablyMobile(emailOrMobile)) {
            otpService.deleteAllExceptVerified(null, emailOrMobile);
        }
    }
    
    public void registerTherapist(TherapistRegisterRequest req) {

        // 1️⃣ Check OTP is verified
        boolean isVerified = otpService.isVerified(req.getEmail(), req.getMobile());

        if (!isVerified) {
            throw new RuntimeException("Please verify your email or mobile before registering.");
        }

        // 2️⃣ Check duplicates
        therapistRepository.findByEmail(req.getEmail())
                .ifPresent(t -> {
                    throw new RuntimeException("Email already registered");
                });

        therapistRepository.findByMobile(req.getMobile())
                .ifPresent(t -> {
                    throw new RuntimeException("Mobile already registered");
                });

        // 3️⃣ Save Therapist
        Therapist therapist = Therapist.builder()
                .fullName(req.getFullName())
                .email(req.getEmail())
                .mobile(req.getMobile())
                .password(passwordEncoder.encode(req.getPassword()))
                .specialization(req.getSpecialization())
                .city(req.getCity())
                .sessionPrice(req.getSessionPrice())
                .bio(req.getBio())
                .verified(true)
                .role(Role.THERAPIST)// OTP already verified
                
                .build();

        therapistRepository.save(therapist);
    }
    
    
    public TherapistLoginResponse loginTherapist(String emailOrMobile, String password) {

        // 1️⃣ Find therapist by email or mobile
        Therapist therapist = therapistRepository.findByEmail(emailOrMobile)
                .or(() -> therapistRepository.findByMobile(emailOrMobile))
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

        // 2️⃣ Check verification
        if (!therapist.isVerified()) {
            throw new RuntimeException("Please verify your account before login.");
        }

        // 3️⃣ Validate password
        if (!passwordEncoder.matches(password, therapist.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // 4️⃣ Create custom therapist user details
        CustomTherapistDetails details = new CustomTherapistDetails(therapist);

        // 5️⃣ Generate JWT token
        String token = jwtService.generateToken(details);

        return new TherapistLoginResponse(true, token, "Login successful", therapist.getRole());
    }



}
