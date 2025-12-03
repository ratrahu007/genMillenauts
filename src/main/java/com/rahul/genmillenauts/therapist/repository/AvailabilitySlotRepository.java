package com.rahul.genmillenauts.therapist.repository;

import com.rahul.genmillenauts.therapist.entity.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findByTherapistIdAndDateAndBookedFalse(Long therapistId, LocalDate date);
    
    List<AvailabilitySlot> findByTherapistId(Long therapistId);

	List<AvailabilitySlot> findByTherapistIdAndDateAfterAndBookedFalse(Long therapistId, LocalDate date);
}
