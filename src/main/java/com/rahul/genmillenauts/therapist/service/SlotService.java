package com.rahul.genmillenauts.therapist.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rahul.genmillenauts.therapist.dto.SlotRequest;
import com.rahul.genmillenauts.therapist.entity.AvailabilitySlot;
import com.rahul.genmillenauts.therapist.entity.Therapist;
import com.rahul.genmillenauts.therapist.repository.AvailabilitySlotRepository;
import com.rahul.genmillenauts.therapist.repository.TherapistRepository;
import com.rahul.genmillenauts.therapist.strategy.SlotGenerationStrategy;
import com.rahul.genmillenauts.therapist.strategy.WeeklySlotGenerationStrategy;

@Service
public class SlotService {

    private static final Logger log = LoggerFactory.getLogger(SlotService.class);

    private final TherapistRepository therapistRepo;
    private final AvailabilitySlotRepository slotRepository;
    private final SlotGenerationStrategy slotStrategy;

    public SlotService(
            TherapistRepository therapistRepo,
            WeeklySlotGenerationStrategy slotStrategy,
            AvailabilitySlotRepository slotRepository
    ) {
        this.therapistRepo = therapistRepo;
        this.slotRepository = slotRepository;
        this.slotStrategy = slotStrategy;
    }

    public void generateSlots(Long therapistId, SlotRequest req) {
        try {
            log.info("🔧 SlotService.generateSlots(therapistId={})", therapistId);

            Therapist therapist = therapistRepo.findById(therapistId)
                    .orElseThrow(() -> new RuntimeException("Therapist not found"));

            slotStrategy.generate(therapist, req);

            log.info("✅ Slots generated in DB for therapist {}", therapistId);

        } catch (Exception e) {
            log.error("❌ Error in SlotService.generateSlots: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<AvailabilitySlot> getSlotsForTherapist(Long therapistId) {
        try {
            log.info("🔧 Fetching slots for therapist {}", therapistId);

            List<AvailabilitySlot> slots =
                    slotRepository.findByTherapistIdAndDateAfterAndBookedFalse(
                            therapistId,
                            LocalDate.now().minusDays(1)
                    );

            log.info("📌 {} available slots found for therapist {}", slots.size(), therapistId);

            return slots;

        } catch (Exception e) {
            log.error("❌ Error in SlotService.getSlotsForTherapist: {}", e.getMessage(), e);
            throw e;
        }
    }
}
