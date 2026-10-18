
package com.rahul.genmillenauts.userservice.userserviceinner;

import com.rahul.genmillenauts.global.config.CustomUserDetails;
import com.rahul.genmillenauts.userservice.entity.User;
import com.rahul.genmillenauts.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_WhenEmailExists_ShouldReturnCustomUserDetails() {

        // Arrange
        String email = "rahul@example.com";
        User user = mock(User.class);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        // Act
        UserDetails result =
                customUserDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(result);
        assertInstanceOf(CustomUserDetails.class, result);

        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).findByMobile(anyString());
    }

    @Test
    void loadUserByUsername_WhenMobileExists_ShouldReturnCustomUserDetails() {

        // Arrange
        String mobile = "9876543210";
        User user = mock(User.class);

        when(userRepository.findByMobile(mobile))
                .thenReturn(Optional.of(user));

        // Act
        UserDetails result =
                customUserDetailsService.loadUserByUsername(mobile);

        // Assert
        assertNotNull(result);
        assertInstanceOf(CustomUserDetails.class, result);

        verify(userRepository, times(1)).findByMobile(mobile);
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void loadUserByUsername_WhenEmailDoesNotExist_ShouldThrowException() {

        // Arrange
        String email = "unknown@example.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(email)
        );

        assertEquals(
                "User not found with email: " + email,
                exception.getMessage()
        );

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void loadUserByUsername_WhenMobileDoesNotExist_ShouldThrowException() {

        // Arrange
        String mobile = "9999999999";

        when(userRepository.findByMobile(mobile))
                .thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(mobile)
        );

        assertEquals(
                "User not found with mobile: " + mobile,
                exception.getMessage()
        );

        verify(userRepository, times(1)).findByMobile(mobile);
    }
}
