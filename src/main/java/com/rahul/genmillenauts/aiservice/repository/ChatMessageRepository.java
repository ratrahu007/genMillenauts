package com.rahul.genmillenauts.aiservice.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.rahul.genmillenauts.aiservice.entity.ChatMessage;
import com.rahul.genmillenauts.userservice.entity.User;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByUserOrderByTimestampAsc(User user);
}
