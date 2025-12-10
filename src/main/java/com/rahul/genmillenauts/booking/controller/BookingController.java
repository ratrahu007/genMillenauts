package com.rahul.genmillenauts.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rahul.genmillenauts.booking.dto.BookingRequestDTO;
import com.rahul.genmillenauts.booking.dto.BookingResponseDTO;
import com.rahul.genmillenauts.booking.service.BookingService;
import com.rahul.genmillenauts.global.config.CustomUserDetails;
import com.rahul.genmillenauts.therapist.service.CustomTherapistDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<BookingResponseDTO> createBooking(
            @RequestBody BookingRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails loggedInUser
    ) {
        dto.setUserId(loggedInUser.getId());
        return ResponseEntity.ok(bookingService.createBooking(dto));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDTO> getBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomUserDetails loggedInUser
    ) {
        BookingResponseDTO res = bookingService.getBookingDetails(bookingId);

        if (!res.getUserId().equals(loggedInUser.getId())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(res);
    }
    
    @GetMapping("/therapist/my")
    public ResponseEntity<?> getMyBookings(
            @AuthenticationPrincipal CustomTherapistDetails loggedInTherapist
    ) {
        Long therapistId = loggedInTherapist.getId();
        return ResponseEntity.ok(bookingService.getBookingsForTherapist(therapistId));
    }


}
