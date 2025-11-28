// src/main/java/com/rahul/genmillenauts/aiservice/repository/StressLogRepository.java
package com.rahul.genmillenauts.aiservice.repository;

import com.rahul.genmillenauts.aiservice.entity.StressLog;
import com.rahul.genmillenauts.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*
  Repository for storing stress calculation events
*/
public interface StressLogRepository extends JpaRepository<StressLog, Long> {

    List<StressLog> findTop10ByUserOrderByCreatedAtDesc(User user);

    StressLog findTop1ByUserOrderByCreatedAtDesc(User user);
    
    StressLog findFirstByUserOrderByCreatedAtDesc(User user);
}
