package com.rahul.genmillenauts.therapist.service;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.rahul.genmillenauts.therapist.entity.Therapist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomTherapistDetails implements UserDetails {

    private final Therapist therapist;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_THERAPIST"));
    }

    @Override
    public String getPassword() {
        return therapist.getPassword();
    }

    @Override
    public String getUsername() {
        return therapist.getEmail(); // or mobile
    }

    public Long getId() {
        return therapist.getId();
    }

    public String getRoleName() {
        return "THERAPIST";
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
