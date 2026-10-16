package com.rahul.genmillenauts.userservice.userserviceinner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.rahul.genmillenauts.global.config.CustomUserDetails;
import com.rahul.genmillenauts.global.service.JwtService;
import com.rahul.genmillenauts.global.service.MessageService;
import com.rahul.genmillenauts.global.service.OtpService;
import com.rahul.genmillenauts.userservice.dto.LoginResponse;
import com.rahul.genmillenauts.userservice.dto.UserUpdateDto;
import com.rahul.genmillenauts.userservice.entity.User;
import com.rahul.genmillenauts.userservice.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserserviceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpService otpService;

    @Mock
    private MessageService messageService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private Userservice userservice;

    @Test
    void registerUserVerifiesOtpHashesPasswordAndSavesUser() {
        User user = User.builder()
                .email("user@example.com")
                .mobile("9876543210")
                .password("plain-password")
                .build();
        when(otpService.isVerified(user.getEmail(), user.getMobile())).thenReturn(true);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByMobile(user.getMobile())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User saved = userservice.registerUser(user);

        assertEquals(user, saved);
        assertNotEquals("plain-password", saved.getPassword());
        assertTrue(new BCryptPasswordEncoder().matches("plain-password", saved.getPassword()));
        verify(userRepository).save(user);
    }

    @Test
    void registerUserRejectsUnverifiedUser() {
        User user = User.builder().email("user@example.com").mobile("9876543210").build();
        when(otpService.isVerified(user.getEmail(), user.getMobile())).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> userservice.registerUser(user));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginReturnsTokenForValidCredentials() {
        User user = User.builder()
                .email("user@example.com")
                .password(new BCryptPasswordEncoder().encode("plain-password"))
                .role("USER")
                .build();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(CustomUserDetails.class))).thenReturn("jwt-token");

        LoginResponse response = userservice.login(user.getEmail(), null, "plain-password");

        assertEquals("jwt-token", response.getToken());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals("USER", response.getRole());
    }

    @Test
    void loginRejectsInvalidPassword() {
        User user = User.builder()
                .email("user@example.com")
                .password(new BCryptPasswordEncoder().encode("correct-password"))
                .build();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userservice.login(user.getEmail(), null, "wrong-password"));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verify(jwtService, never()).generateToken(any(CustomUserDetails.class));
    }

    @Test
    void updateUserProfileUpdatesProvidedFields() {
        User user = User.builder().id(1L).city("Old City").isOptedForOnlineMeets(false).build();
        UserUpdateDto update = new UserUpdateDto("New City", true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserUpdateDto result = userservice.updateUserProfile(1L, update);

        assertEquals("New City", result.getCity());
        assertEquals(true, result.getIsOptedForOfflineMeets());
        assertEquals("New City", user.getCity());
        assertTrue(user.isOptedForOnlineMeets());
        verify(userRepository).save(user);
    }
}
