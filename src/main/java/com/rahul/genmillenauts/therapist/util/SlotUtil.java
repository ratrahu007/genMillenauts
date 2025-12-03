package com.rahul.genmillenauts.therapist.util;

import java.time.LocalDate;
import java.time.LocalTime;

import com.rahul.genmillenauts.therapist.entity.AvailabilitySlot;
import com.rahul.genmillenauts.therapist.entity.Therapist;

public class SlotUtil {

    public static boolean isValid(LocalTime start, LocalTime end, int duration) {
        return !start.plusMinutes(duration).isAfter(end);
    }

    public static AvailabilitySlot buildSlot(
            Therapist therapist,
            LocalDate date,
            LocalTime start,
            int durationMinutes
    ) {
        AvailabilitySlot slot = new AvailabilitySlot();
        slot.setTherapist(therapist);
        slot.setDate(date);
        slot.setStartTime(start);
        slot.setEndTime(start.plusMinutes(durationMinutes));
        slot.setBooked(false);

        return slot;
    }
}
