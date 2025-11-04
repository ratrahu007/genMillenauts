package com.rahul.genmillenauts.userservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.rahul.genmillenauts.userservice.entity.OtpData;

import jakarta.transaction.Transactional;

@Repository
public interface OtpRepository extends JpaRepository<OtpData, Long> {

    Optional<OtpData> findByEmail(String email);

    Optional<OtpData> findByMobile(String mobile);

    // Check if OTP is VERIFIED before allowing registration
    Optional<OtpData> findByEmailAndStatus(String email, String status);

    Optional<OtpData> findByMobileAndStatus(String mobile, String status);

    Optional<OtpData> findTopByEmailAndStatusOrderByCreatedAtDesc(String email, String status);

    Optional<OtpData> findTopByMobileAndStatusOrderByCreatedAtDesc(String mobile, String status);

    @Modifying
    @Transactional
    void deleteAllByEmailAndStatus(String email, String status);

    @Modifying
    @Transactional
    void deleteAllByMobileAndStatus(String mobile, String status);

    // ✅ New: verify with both OTP + email/mobile
    Optional<OtpData> findByEmailAndOtpAndStatus(String email, String otp, String status);

    Optional<OtpData> findByMobileAndOtpAndStatus(String mobile, String otp, String status);
}
