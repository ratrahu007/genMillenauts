package com.rahul.genmillenauts.therapist.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rahul.genmillenauts.global.dto.ApiResponse;
import com.rahul.genmillenauts.therapist.dto.TherapistProfileResponse;
import com.rahul.genmillenauts.therapist.dto.TherapistProfileUpdateRequest;
import com.rahul.genmillenauts.therapist.service.CustomTherapistDetails;
import com.rahul.genmillenauts.therapist.service.TherapistService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/therapists")
@RequiredArgsConstructor
public class TherapistProfileController {

    private final TherapistService therapistService;

    // ---------- GET PROFILE ----------
    @GetMapping("/me")
    public ResponseEntity<TherapistProfileResponse> getMyProfile(
            @AuthenticationPrincipal CustomTherapistDetails therapist
    ) {
        Long therapistId = therapist.getId();
        return ResponseEntity.ok(therapistService.getProfile(therapistId));
    }

    // ---------- UPDATE PROFILE ----------
    @PutMapping("/me")
    public ResponseEntity<TherapistProfileResponse> updateMyProfile(
            @AuthenticationPrincipal CustomTherapistDetails therapist,
            @RequestBody TherapistProfileUpdateRequest req
    ) {
        Long therapistId = therapist.getId();
        return ResponseEntity.ok(therapistService.updateProfile(therapistId, req));
    }
    
    
    @GetMapping("/public")
    public ResponseEntity<List<TherapistProfileResponse>> getAllTherapists() {
        return ResponseEntity.ok(therapistService.getAllAvailableTherapists());
    }


    // ---------- DELETE PROFILE ----------
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse> deleteMyProfile(
            @AuthenticationPrincipal CustomTherapistDetails therapist
    ) {
        Long therapistId = therapist.getId();
        therapistService.deleteProfile(therapistId);

        return ResponseEntity.ok(
                new ApiResponse(true, "Therapist profile deleted successfully")
        );
    }
}
