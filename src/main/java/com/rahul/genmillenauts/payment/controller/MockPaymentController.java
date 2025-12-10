package com.rahul.genmillenauts.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rahul.genmillenauts.booking.dto.BookingResponseDTO;
import com.rahul.genmillenauts.global.config.CustomUserDetails;
import com.rahul.genmillenauts.payment.dto.MockPaymentDTO;
import com.rahul.genmillenauts.payment.service.MockPaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class MockPaymentController {

    private final MockPaymentService paymentService;

    @PostMapping("/mock-pay")
    public ResponseEntity<BookingResponseDTO> mockPay(
            @RequestBody MockPaymentDTO dto,
            @AuthenticationPrincipal CustomUserDetails loggedInUser
    ) {
        BookingResponseDTO response = paymentService.processMockPayment(dto, loggedInUser.getId());
        return ResponseEntity.ok(response);
    }
}
