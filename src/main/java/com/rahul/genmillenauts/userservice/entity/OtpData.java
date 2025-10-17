package com.rahul.genmillenauts.userservice.entity;

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
    private String otp; // OTP code sent to email or mobile
    
    
    private String mobile;
    
    
    private String email;

    @Column(nullable = false)
    private String status; // PENDING, VERIFIED, EXPIRED, INVALID

    
    private String type; // EMAIL or MOBILE

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user; // Linked user, optional before registration
}
