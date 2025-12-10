package com.rahul.genmillenauts.therapist.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rahul.genmillenauts.therapist.entity.Therapist;

public interface TherapistRepository  extends JpaRepository<Therapist, Long> {
	 Optional<Therapist> findByEmail(String email);
	    Optional<Therapist> findByMobile(String mobile);
	    boolean existsByEmail(String email);
	    boolean existsByMobile(String mobile);
	    
	    List<Therapist> findByVerifiedTrue();


}
