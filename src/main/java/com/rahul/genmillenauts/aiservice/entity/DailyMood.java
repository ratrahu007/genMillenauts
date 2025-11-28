// src/main/java/com/rahul/genmillenauts/aiservice/entity/DailyMood.java
package com.rahul.genmillenauts.aiservice.entity;

import com.rahul.genmillenauts.userservice.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/*
  DailyMood entity: aggregated daily mood/stress for a user (one record per user per date)
*/
@Entity
@Table(name = "daily_mood",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "date"})})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyMood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    private int averageStress;

    private String overallMood;  // GOOD, OKAY, BAD, SEVERE

    private LocalDate date;
}
