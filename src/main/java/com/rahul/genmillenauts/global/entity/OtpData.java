package com.rahul.genmillenauts.global.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String otp; // 6-digit OTP code

    private String mobile;
    private String email;

    @Column(nullable = false)
    private String type; // EMAIL or MOBILE

    @Column(nullable = false)
    private String status; // PENDING, VERIFIED, EXPIRED, INVALID

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
