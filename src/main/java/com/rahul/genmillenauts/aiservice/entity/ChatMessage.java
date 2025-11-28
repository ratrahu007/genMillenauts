package com.rahul.genmillenauts.aiservice.entity;

import java.time.LocalDateTime;

import com.rahul.genmillenauts.userservice.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Proper FK relationship
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String message;

    private boolean isBot;
    
    
    private LocalDateTime createdAt;

}
