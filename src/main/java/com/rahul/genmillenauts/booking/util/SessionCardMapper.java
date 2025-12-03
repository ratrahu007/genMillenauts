package com.rahul.genmillenauts.booking.util;

import org.aspectj.weaver.patterns.ConcreteCflowPointcut.Slot;

import com.rahul.genmillenauts.booking.dto.TherapistSessionCardDTO;
import com.rahul.genmillenauts.booking.dto.UserSessionCardDTO;
import com.rahul.genmillenauts.booking.entity.Booking;
import com.rahul.genmillenauts.therapist.entity.Therapist;
import com.rahul.genmillenauts.userservice.entity.User;

public class SessionCardMapper {

    // Polymorphism Overloading (same method name, different objects)
    public static UserSessionCardDTO toUserCard(
            Booking booking,
            Slot slot,
            Therapist therapist
    ) {
        return UserSessionCardDTO.builder()
                .bookingId(booking.getBookingId())
                .therapistName(therapist.getFullName())
                .slotDate(slot.getSlotDate().toString())
                .slotTime(slot.getSlotTime().toString())
                .sessionLink(booking.getJitsiUrl())
                .status(booking.getStatus().name())
                .build();
    }

    public static TherapistSessionCardDTO toTherapistCard(
            Booking booking,
            Slot slot,
            User user
    ) {
        return TherapistSessionCardDTO.builder()
                .bookingId(booking.getBookingId())
                .userName(user.getFullName())
                .slotDate(slot.getSlotDate().toString())
                .slotTime(slot.getSlotTime().toString())
                .sessionLink(booking.getJitsiUrl())
                .status(booking.getStatus().name())
                .build();
    }
}
