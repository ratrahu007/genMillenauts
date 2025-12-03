package com.rahul.genmillenauts.global.config;

import com.rahul.genmillenauts.global.service.JwtService;
import com.rahul.genmillenauts.userservice.userserviceinner.CustomUserDetailsService;
import com.rahul.genmillenauts.therapist.repository.TherapistRepository;
import com.rahul.genmillenauts.therapist.service.CustomTherapistDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final TherapistRepository therapistRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // --------------------------
        // PUBLIC ENDPOINTS (Skip JWT)
        // --------------------------
        if (path.startsWith("/api/auth/") ||
            path.startsWith("/api/therapists/send-otp") ||
            path.startsWith("/api/therapists/verify-otp") ||
            path.startsWith("/api/therapists/register") ||
            path.startsWith("/api/therapists/login")) {

            filterChain.doFilter(request, response);
            return;
        }

        // --------------------------
        // EXTRACT AUTH TOKEN
        // --------------------------
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // Extract username + role
            String username = jwtService.extractUsername(token);
            String role = jwtService.extractAllClaims(token).get("role", String.class);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // -------------------------------------------------
                // USER LOGIN
                // -------------------------------------------------
                if ("USER".equals(role)) {

                    var userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtService.isTokenValid(token)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        Long userId = jwtService.extractAllClaims(token).get("userId", Long.class);
                        request.setAttribute("userId", userId);
                    }
                }

                // -------------------------------------------------
                // THERAPIST LOGIN
                // -------------------------------------------------
                else if ("THERAPIST".equals(role)) {

                    var therapist = therapistRepository.findByEmail(username)
                            .or(() -> therapistRepository.findByMobile(username))
                            .orElseThrow(() -> new RuntimeException("Therapist not found"));

                    CustomTherapistDetails therapistDetails = new CustomTherapistDetails(therapist);

                    if (jwtService.isTokenValid(token)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        therapistDetails,
                                        null,
                                        therapistDetails.getAuthorities()
                                );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        request.setAttribute("therapistId", therapistDetails.getId());
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("⚠️ JWT FILTER ERROR: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
