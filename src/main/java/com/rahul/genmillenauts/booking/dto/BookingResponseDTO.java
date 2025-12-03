package com.rahul.genmillenauts.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO → never expose entity directly
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponseDTO {

    private Long bookingId;
    private Long userId;
    private Long therapistId;
    private Long slotId;
    private String status;
    private String jitsiUrl;
}
