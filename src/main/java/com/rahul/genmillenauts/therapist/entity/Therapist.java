package com.rahul.genmillenauts.therapist.entity;

import com.rahul.genmillenauts.global.config.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "therapists")
public class Therapist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String mobile;

    private String password;
    private String specialization;
    private String city;
    private double sessionPrice;
    private String bio;
    private String profilePictureUrl;
    
    @Enumerated(EnumType.STRING)
    public  Role role;

    
    private boolean verified = false;

   
    // Getters and Setters
}
