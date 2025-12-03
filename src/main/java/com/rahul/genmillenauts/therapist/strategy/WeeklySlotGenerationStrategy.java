package com.rahul.genmillenauts.therapist.strategy;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Component;

import com.rahul.genmillenauts.therapist.dto.SlotRequest;
import com.rahul.genmillenauts.therapist.entity.AvailabilitySlot;
import com.rahul.genmillenauts.therapist.entity.Therapist;
import com.rahul.genmillenauts.therapist.repository.AvailabilitySlotRepository;
import com.rahul.genmillenauts.therapist.util.SlotUtil;

@Component
public class WeeklySlotGenerationStrategy implements SlotGenerationStrategy {

    private final AvailabilitySlotRepository slotRepository;

    public WeeklySlotGenerationStrategy(AvailabilitySlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    @Override
    public void generate(Therapist therapist, SlotRequest req) {

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(30);

        DayOfWeek targetDay = DayOfWeek.valueOf(req.getDayOfWeek().toUpperCase());
        LocalTime start = LocalTime.parse(req.getStartTime());
        LocalTime end = LocalTime.parse(req.getEndTime());
        int duration = req.getDurationMinutes();

        for (LocalDate date = today; date.isBefore(endDate); date = date.plusDays(1)) {

            if (!date.getDayOfWeek().equals(targetDay)) continue;

            LocalTime current = start;

            while (SlotUtil.isValid(current, end, duration)) {

                AvailabilitySlot slot =
                        SlotUtil.buildSlot(therapist, date, current, duration);

                slotRepository.save(slot);

                current = current.plusMinutes(duration);
            }
        }
    }
}
