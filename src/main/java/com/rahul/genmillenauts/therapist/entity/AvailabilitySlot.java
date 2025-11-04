package com.rahul.genmillenauts.therapist.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "availability_slots")
public class AvailabilitySlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "therapist_id")
    private Therapist therapist;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean booked = false;

    // Getters and Setters
}
