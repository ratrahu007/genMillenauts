package com.rahul.genmillenauts.booking.dto;

import com.rahul.genmillenauts.userservice.dto.AlertContactDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// DTO = Industry standard for request payloads
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Therapist ID is required")
    private Long therapistId;

    @NotNull(message = "Slot ID is required")
    private Long slotId;
}
