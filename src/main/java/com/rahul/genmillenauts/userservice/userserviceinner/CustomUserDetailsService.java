package com.rahul.genmillenauts.userservice.userserviceinner;

import com.rahul.genmillenauts.userservice.entity.User;
import com.rahul.genmillenauts.userservice.repository.UserRepository;
import com.rahul.genmillenauts.global.config.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        boolean isEmail = identifier.contains("@");

        User user = isEmail
                ? userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + identifier))
                : userRepository.findByMobile(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with mobile: " + identifier));

        // ✅ Return our custom implementation instead of Spring's default User
        return new CustomUserDetails(user);
    }
}
