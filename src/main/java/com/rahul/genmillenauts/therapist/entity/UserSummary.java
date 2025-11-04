package com.rahul.genmillenauts.therapist.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_summaries")
public class UserSummary {

    @Id
    private Long id; // same as user's ID in user module

    private String fullName;
    private String email;

    // Getters and Setters
}
