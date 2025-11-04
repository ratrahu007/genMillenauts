package com.rahul.genmillenauts.therapist.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "therapist_id")
    private Therapist therapist;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserSummary user;

    @OneToOne
    @JoinColumn(name = "slot_id")
    private AvailabilitySlot slot;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Payment payment;

    private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED
    private LocalDateTime bookedAt = LocalDateTime.now();

    // Getters and Setters
}
