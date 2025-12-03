package com.rahul.genmillenauts.booking.factory;

import com.rahul.genmillenauts.booking.entity.Booking;
import com.rahul.genmillenauts.booking.entity.BookingStatus;
import com.rahul.genmillenauts.therapist.entity.AvailabilitySlot;
import com.rahul.genmillenauts.therapist.entity.Therapist;
import com.rahul.genmillenauts.userservice.entity.User;

public class BookingFactory {

    public static Booking createPendingBooking(
            User user,
            Therapist therapist,
            AvailabilitySlot slot) {

        return Booking.builder()
                .user(user)
                .therapist(therapist)
                .slot(slot)
                .status(BookingStatus.PENDING)
                .bookedAt(java.time.LocalDateTime.now())
                .build();
    }
}
