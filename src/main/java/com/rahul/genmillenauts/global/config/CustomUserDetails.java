package com.rahul.genmillenauts.global.config;



import com.rahul.genmillenauts.userservice.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Custom implementation of Spring Security's UserDetails
 * that carries extra fields like id, email, mobile, role.
 */
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String mobile;
    private final String password;
    private final String role;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.mobile = user.getMobile();
        this.password = user.getPassword();
        this.role = user.getRole();
    }

    // --- Extra fields for convenience ---
    public Long getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public String getMobile() {
        return mobile;
    }
    public String getRoleName() {
        return role;
    }

    // --- UserDetails interface methods ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + role);
    }

    @Override
    public String getPassword() {
        return password;
    }

    // Spring uses this as "username" → we fallback to email or mobile
    @Override
    public String getUsername() {
        return email != null ? email : mobile;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
