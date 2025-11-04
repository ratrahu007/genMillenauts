package com.rahul.genmillenauts.therapist.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "therapists")
public class Therapist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String password;
    private String specialization;
    private String city;
    private double sessionPrice;
    private String bio;
    private String profilePictureUrl;
    private boolean verified = false;

    @OneToMany(mappedBy = "therapist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailabilitySlot> slots = new ArrayList<>();

    @OneToMany(mappedBy = "therapist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    // Getters and Setters
}
