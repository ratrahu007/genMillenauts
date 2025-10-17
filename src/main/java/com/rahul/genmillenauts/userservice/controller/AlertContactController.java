package com.rahul.genmillenauts.userservice.controller;

import com.rahul.genmillenauts.global.config.CustomUserDetails;
import com.rahul.genmillenauts.userservice.dto.AlertContactDto;
import com.rahul.genmillenauts.userservice.userserviceinner.AlertContactService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users/me/alerts")
@RequiredArgsConstructor
public class AlertContactController {

    private final AlertContactService alertContactService;

    // ✅ Add a new alert contact (SMS will be sent automatically)
    @PostMapping
    public ResponseEntity<AlertContactDto> addContact(
            @RequestBody @Valid AlertContactDto dto,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(alertContactService.addContact(currentUser.getId(), dto));
    }

    // ✅ List all alert contacts
    @GetMapping
    public ResponseEntity<List<AlertContactDto>> listContacts(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(alertContactService.listContacts(currentUser.getId()));
    }

    // ✅ Delete an alert contact
    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> deleteContact(
            @PathVariable Long contactId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        alertContactService.deleteContact(currentUser.getId(), contactId);
        return ResponseEntity.noContent().build();
    }
}
