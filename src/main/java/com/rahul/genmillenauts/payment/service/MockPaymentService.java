package com.rahul.genmillenauts.payment.service;

import org.springframework.stereotype.Service;

import com.rahul.genmillenauts.booking.dto.BookingResponseDTO;
import com.rahul.genmillenauts.booking.service.BookingService;
import com.rahul.genmillenauts.payment.dto.MockPaymentDTO;

@Service
public class MockPaymentService {

    private final BookingService bookingService;

    public MockPaymentService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public BookingResponseDTO processMockPayment(MockPaymentDTO dto, Long loggedInUserId) {

        BookingResponseDTO booking = bookingService.getBookingDetails(dto.getBookingId());

        if (!booking.getUserId().equals(loggedInUserId)) {
            throw new RuntimeException("You are not allowed to pay for this booking");
        }

        String jitsiUrl = "https://meet.jit.si/session_" + dto.getBookingId();

        return bookingService.confirmBooking(dto.getBookingId(), jitsiUrl);
    }

}
