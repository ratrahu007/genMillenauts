package com.rahul.genmillenauts.therapist.service;

import org.springframework.stereotype.Service;

import com.rahul.genmillenauts.therapist.dto.TherapistProfileResponse;
import com.rahul.genmillenauts.therapist.dto.TherapistProfileUpdateRequest;
import com.rahul.genmillenauts.therapist.entity.Therapist;
import com.rahul.genmillenauts.therapist.repository.TherapistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TherapistService {

    private final TherapistRepository therapistRepository;

    // ---------- GET PROFILE ----------
    public TherapistProfileResponse getProfile(Long id) {

        Therapist t = therapistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

        return mapToResponse(t);
    }

    // ---------- UPDATE PROFILE ----------
    public TherapistProfileResponse updateProfile(Long id, TherapistProfileUpdateRequest req) {

        Therapist t = therapistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

        if (req.getFullName() != null) t.setFullName(req.getFullName());
        if (req.getMobile() != null) t.setMobile(req.getMobile());
        if (req.getSpecialization() != null) t.setSpecialization(req.getSpecialization());
        if (req.getCity() != null) t.setCity(req.getCity());
        if (req.getBio() != null) t.setBio(req.getBio());
        if (req.getSessionPrice() != null) t.setSessionPrice(req.getSessionPrice());
        if (req.getProfilePhotoUrl() != null) t.setProfilePictureUrl(req.getProfilePhotoUrl());

        therapistRepository.save(t);

        return mapToResponse(t);
    }

    // ---------- DELETE PROFILE ----------
    public void deleteProfile(Long id) {

        Therapist t = therapistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

        t.setVerified(false); // soft delete
        therapistRepository.save(t);
    }

    // ---------- MAPPER ----------
    private TherapistProfileResponse mapToResponse(Therapist t) {
        return TherapistProfileResponse.builder()
                .id(t.getId())
                .fullName(t.getFullName())
                .email(t.getEmail())
                .mobile(t.getMobile())
                .specialization(t.getSpecialization())
                .city(t.getCity())
                .bio(t.getBio())
                .sessionPrice(t.getSessionPrice())
                .profilePhotoUrl(t.getProfilePictureUrl())
                .build();
    }
}

