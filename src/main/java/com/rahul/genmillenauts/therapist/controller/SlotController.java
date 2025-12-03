package com.rahul.genmillenauts.therapist.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rahul.genmillenauts.global.dto.ApiResponse;
import com.rahul.genmillenauts.therapist.dto.SlotRequest;
import com.rahul.genmillenauts.therapist.entity.AvailabilitySlot;
import com.rahul.genmillenauts.therapist.service.CustomTherapistDetails;
import com.rahul.genmillenauts.therapist.service.SlotService;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    private static final Logger log = LoggerFactory.getLogger(SlotController.class);

    private final SlotService slotService;

    @Autowired
    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    /**
     * THERAPIST GENERATES SLOTS
     */
    @PostMapping("/generate")
    public ApiResponse generateSlots(
            @AuthenticationPrincipal CustomTherapistDetails therapist,
            @RequestBody SlotRequest req
    ) {
        try {
            log.info("📥 /slots/generate → Therapist: {}", therapist);

            if (therapist == null) {
                log.error("❌ Therapist = NULL (token not sent or expired)");
                return new ApiResponse(false, "Unauthorized: therapist missing in token");
            }

            slotService.generateSlots(therapist.getId(), req);
            log.info("✅ Slots generated successfully for therapist {}", therapist.getId());

            return new ApiResponse(true, "Slots generated successfully!");

        } catch (Exception e) {
            log.error("❌ Error in /slots/generate: {}", e.getMessage(), e);
            return new ApiResponse(false, e.getMessage());
        }
    }

    /**
     * THERAPIST FETCHES SLOTS
     */
    @PostMapping("/fetch")
    public Object fetchSlots(
            @AuthenticationPrincipal CustomTherapistDetails therapist
    ) {
        try {
            log.info("📥 /slots/fetch → Therapist: {}", therapist);

            if (therapist == null) {
                log.error("❌ Therapist = NULL (frontend missing Authorization header)");
                return new ApiResponse(false, "Unauthorized: therapist missing in token");
            }

            List<AvailabilitySlot> slots = slotService.getSlotsForTherapist(therapist.getId());
            log.info("📤 Fetched {} slots for therapist {}", slots.size(), therapist.getId());

            return slots;

        } catch (Exception e) {
            log.error("❌ Error in /slots/fetch: {}", e.getMessage(), e);
            return new ApiResponse(false, e.getMessage());
        }
    }
}
