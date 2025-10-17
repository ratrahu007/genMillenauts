package com.rahul.genmillenauts.aiservice.repository;

import com.rahul.genmillenauts.aiservice.entity.MoodLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MoodLogRepository extends JpaRepository<MoodLog, Long> {
    List<MoodLog> findByUserIdOrderByCreatedAtDesc(String  userId);
}
