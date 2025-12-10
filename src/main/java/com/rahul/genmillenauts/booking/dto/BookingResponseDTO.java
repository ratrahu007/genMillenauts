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
    private String userName;

    private Long therapistId;
    private String therapistName;
    private String therapistSpeciality; // optional

    private Long slotId;
    private String slotDate;
    private String slotTime;

    private String status;
    private String jitsiUrl;
}
