package com.rahul.genmillenauts.booking.service.impl;

import java.util.List;

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

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Therapist therapist = therapistRepository.findById(req.getTherapistId())
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

        AvailabilitySlot slot = slotRepository.findById(req.getSlotId())
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (slot.isBooked()) {
            throw new RuntimeException("Slot is already booked");
        }

        Booking booking = BookingFactory.createPendingBooking(user, therapist, slot);

        Booking saved = bookingRepository.save(booking);

        return mapToResponse(saved);
    }

    @Override
    public BookingResponseDTO confirmBooking(Long bookingId, String jitsiUrl) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setJitsiRoomUrl(jitsiUrl);

        AvailabilitySlot slot = booking.getSlot();
        slot.setBooked(true);
        slotRepository.save(slot);

        Booking updated = bookingRepository.save(booking);

        return mapToResponse(updated);
    }

    @Override
    public BookingResponseDTO getBookingDetails(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        return mapToResponse(booking);
    }

    @Override
    public List<BookingResponseDTO> getBookingsForTherapist(Long therapistId) {

        List<Booking> bookings = bookingRepository.findByTherapist_Id(therapistId);

        return bookings.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<BookingResponseDTO> getBookingsForUser(Long userId) {
        List<Booking> bookings = bookingRepository.findByUser_Id(userId);
        return bookings.stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Common mapper for Booking -> BookingResponseDTO
     */
    private BookingResponseDTO mapToResponse(Booking booking) {

        return BookingResponseDTO.builder()
                .bookingId(booking.getBookingId())

                .userId(booking.getUser().getId())
                .userName(booking.getUser().getFullName())

                .therapistId(booking.getTherapist().getId())
                .therapistName(booking.getTherapist().getFullName())
                .therapistSpeciality(booking.getTherapist().getSpecialization())

                .slotId(booking.getSlot().getId())
                .slotDate(booking.getSlot().getDate().toString())
                .slotTime(
                        booking.getSlot().getStartTime().toString()
                                + " - "
                                + booking.getSlot().getEndTime().toString()
                )

                .status(booking.getStatus().name())
                .jitsiUrl(booking.getJitsiRoomUrl())
                .build();
    }
}