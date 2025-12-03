package com.rahul.genmillenauts.booking.service;

import com.rahul.genmillenauts.booking.dto.BookingRequestDTO;
import com.rahul.genmillenauts.booking.dto.BookingResponseDTO;

public interface BookingService {

    BookingResponseDTO createBooking(BookingRequestDTO request);

    BookingResponseDTO confirmBooking(Long bookingId, String jitsiUrl);
}
