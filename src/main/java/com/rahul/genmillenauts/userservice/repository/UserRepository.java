package com.rahul.genmillenauts.userservice.repository;

import com.rahul.genmillenauts.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByMobile(String mobile);
    

    Optional<User> findByAnyName(String anyName);

    // Only allow registration if OTP status is VERIFIED
    Optional<User> findByEmailAndOtpDataStatus(String email, String status);

    Optional<User> findByMobileAndOtpDataStatus(String mobile, String status);
}
