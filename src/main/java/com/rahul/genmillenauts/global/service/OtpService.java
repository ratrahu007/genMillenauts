package com.rahul.genmillenauts.global.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.rahul.genmillenauts.global.entity.OtpData;
import com.rahul.genmillenauts.global.repository.OtpRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final MessageService messageService; // your existing mail/sms service
    private final Random random = new Random();

    // ---------------------- Send OTP ----------------------
    public void sendOtp(String email, String mobile) throws Exception {
        if ((email == null || email.isEmpty()) && (mobile == null || mobile.isEmpty())) {
            throw new Exception("Email or Mobile must be provided");
        }

        String otp = String.format("%06d", random.nextInt(999999));

        OtpData otpData = OtpData.builder()
                .otp(otp)
                .email(email)
                .mobile(mobile)
                .type(email != null ? "EMAIL" : "MOBILE")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        otpRepository.save(otpData);

        if (email != null)
            messageService.sendEmail(email, "Your OTP", "Your verification code is: " + otp);
        if (mobile != null)
            messageService.sendOtpSms(mobile, "Your verification code is: " + otp);
    }

    // ---------------------- Verify OTP ----------------------
    public String verifyOtp(String email, String mobile, String otp) {
        Optional<OtpData> otpOptional = Optional.empty();

        if (email != null)
            otpOptional = otpRepository.findTopByEmailAndStatusOrderByCreatedAtDesc(email, "PENDING");
        else if (mobile != null)
            otpOptional = otpRepository.findTopByMobileAndStatusOrderByCreatedAtDesc(mobile, "PENDING");

        if (otpOptional.isEmpty())
            return "No OTP found or already verified.";

        OtpData otpData = otpOptional.get();

        if (otpData.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpData.setStatus("EXPIRED");
            otpRepository.save(otpData);
            return "OTP expired. Please request a new one.";
        }

        if (otpData.getOtp().equals(otp)) {
            otpData.setStatus("VERIFIED");
            otpRepository.save(otpData);
            return "OTP verified successfully.";
        }

        otpData.setStatus("INVALID");
        otpRepository.save(otpData);
        return "Invalid OTP. Please try again.";
    }

    // Helper: Check verified
    public boolean isVerified(String email, String mobile) {
        Optional<OtpData> verifiedOtp = Optional.empty();
        if (email != null)
            verifiedOtp = otpRepository.findTopByEmailAndStatusOrderByCreatedAtDesc(email, "VERIFIED");
        else if (mobile != null)
            verifiedOtp = otpRepository.findTopByMobileAndStatusOrderByCreatedAtDesc(mobile, "VERIFIED");
        return verifiedOtp.isPresent();
    }
    
    @Transactional
    public void deleteAllExceptVerified(String email, String mobile) {
        if (email != null) {
            otpRepository.deleteByEmailAndStatusNot(email, "VERIFIED");
        } else if (mobile != null) {
            otpRepository.deleteByMobileAndStatusNot(mobile, "VERIFIED");
        }
    }

}
