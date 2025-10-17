// repository/AlertContactRepository.java
package com.rahul.genmillenauts.userservice.repository;

import com.rahul.genmillenauts.userservice.entity.AlertContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertContactRepository extends JpaRepository<AlertContact, Long> {
    List<AlertContact> findByUserId(Long userId);
}
