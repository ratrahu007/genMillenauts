package com.rahul.genmillenauts.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rahul.genmillenauts.booking.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	
    // find all bookings where therapist.id = therapistId
    List<Booking> findByTherapist_Id(Long therapistId);
}
