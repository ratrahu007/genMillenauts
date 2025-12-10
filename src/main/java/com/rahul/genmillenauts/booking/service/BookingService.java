package com.rahul.genmillenauts.booking.service;

import java.util.List;

import com.rahul.genmillenauts.booking.dto.BookingRequestDTO;
import com.rahul.genmillenauts.booking.dto.BookingResponseDTO;

public interface BookingService {

    BookingResponseDTO createBooking(BookingRequestDTO request);

    BookingResponseDTO confirmBooking(Long bookingId, String jitsiUrl);

	BookingResponseDTO getBookingDetails(Long bookingId);
	
	 // get all bookings for logged-in therapist
    List<BookingResponseDTO> getBookingsForTherapist(Long therapistId);
}
