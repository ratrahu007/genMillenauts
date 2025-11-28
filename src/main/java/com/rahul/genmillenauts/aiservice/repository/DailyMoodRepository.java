// src/main/java/com/rahul/genmillenauts/aiservice/repository/DailyMoodRepository.java
package com.rahul.genmillenauts.aiservice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rahul.genmillenauts.aiservice.entity.DailyMood;
import com.rahul.genmillenauts.userservice.entity.User;

/*
  Repository for daily mood aggregation
*/
public interface DailyMoodRepository extends JpaRepository<DailyMood, Long> {

    Optional<DailyMood> findByUserAndDate(User user, LocalDate date);
    
    List<DailyMood> findByUserAndDateBetween(
            User user,
            LocalDate start,
            LocalDate end
    );
}
