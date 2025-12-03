package com.rahul.genmillenauts.booking.entity;

import java.time.LocalDateTime;

import com.rahul.genmillenauts.therapist.entity.AvailabilitySlot;
import com.rahul.genmillenauts.therapist.entity.Therapist;
import com.rahul.genmillenauts.userservice.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "therapist_id")
    private Therapist therapist;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "slot_id")
    private AvailabilitySlot slot;

    private String jitsiRoomUrl;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime bookedAt;
}
