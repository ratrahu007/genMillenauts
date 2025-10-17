package com.rahul.genmillenauts.userservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "alert_contacts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;       // contact name
    private String phone;    // mobile number or email
    private String relation;       // "mobile" or "email"
    
    // Many alert contacts belong to one user
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable= false)
    private User user;
}
