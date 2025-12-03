package com.rahul.genmillenauts.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rahul.genmillenauts.booking.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
