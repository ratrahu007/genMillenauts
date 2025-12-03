package com.rahul.genmillenauts.booking.service.impl;

import org.springframework.stereotype.Service;

import com.rahul.genmillenauts.booking.dto.BookingRequestDTO;
import com.rahul.genmillenauts.booking.dto.BookingResponseDTO;
import com.rahul.genmillenauts.booking.entity.Booking;
import com.rahul.genmillenauts.booking.entity.BookingStatus;
import com.rahul.genmillenauts.booking.exception.BookingNotFoundException;
import com.rahul.genmillenauts.booking.factory.BookingFactory;
import com.rahul.genmillenauts.booking.repository.BookingRepository;
import com.rahul.genmillenauts.booking.service.BookingService;
import com.rahul.genmillenauts.therapist.entity.AvailabilitySlot;
import com.rahul.genmillenauts.therapist.entity.Therapist;
import com.rahul.genmillenauts.therapist.repository.AvailabilitySlotRepository;
import com.rahul.genmillenauts.therapist.repository.TherapistRepository;
import com.rahul.genmillenauts.userservice.entity.User;
import com.rahul.genmillenauts.userservice.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TherapistRepository therapistRepository;
    private final AvailabilitySlotRepository slotRepository;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            TherapistRepository therapistRepository,
            AvailabilitySlotRepository slotRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.therapistRepository = therapistRepository;
        this.slotRepository = slotRepository;
    }

    @Override
    public BookingResponseDTO createBooking(BookingRequestDTO req) {

        log.info("Creating booking for user {}", req.getUserId());

        // Load objects by ID
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Therapist therapist = therapistRepository.findById(req.getTherapistId())
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

        AvailabilitySlot slot = slotRepository.findById(req.getSlotId())
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        // Use factory to build booking
        Booking booking = BookingFactory.createPendingBooking(user, therapist, slot);

        Booking saved = bookingRepository.save(booking);

        return BookingResponseDTO.builder()
                .bookingId(saved.getBookingId())
                .userId(saved.getUser().getId())
                .therapistId(saved.getTherapist().getId())
                .slotId(saved.getSlot().getId())
                .status(saved.getStatus().name())
                .build();
    }

    @Override
    public BookingResponseDTO confirmBooking(Long bookingId, String jitsiUrl) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setJitsiRoomUrl(jitsiUrl);

        Booking updated = bookingRepository.save(booking);

        return BookingResponseDTO.builder()
                .bookingId(updated.getBookingId())
                .userId(updated.getUser().getId())
                .therapistId(updated.getTherapist().getId())
                .slotId(updated.getSlot().getId())
                .status(updated.getStatus().name())
                .jitsiUrl(updated.getJitsiRoomUrl())
                .build();
    }
}
