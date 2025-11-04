package com.rahul.genmillenauts.therapist.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private String orderId;
    private String paymentId;
    private double amount;
    private String currency = "INR";
    private String status; // CREATED, PAID, REFUNDED
    private LocalDateTime paymentDate = LocalDateTime.now();

    // Getters and Setters
}
