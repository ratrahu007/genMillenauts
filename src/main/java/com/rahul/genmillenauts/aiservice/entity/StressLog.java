// src/main/java/com/rahul/genmillenauts/aiservice/entity/StressLog.java
package com.rahul.genmillenauts.aiservice.entity;

import com.rahul.genmillenauts.userservice.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/*
  StressLog entity: stores each stress calculation event (timestamped)
*/
@Entity
@Table(name = "stress_logs")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StressLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private int stressIndex;        // 0-100

    private String mood;            // HAPPY, NEUTRAL, STRESSED, CRISIS

    private LocalDateTime createdAt;
}
