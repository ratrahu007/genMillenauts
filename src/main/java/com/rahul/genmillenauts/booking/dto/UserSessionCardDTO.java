package com.rahul.genmillenauts.booking.dto;

import lombok.*;

// DTO - Standard for returning limited & safe data
@Getter @Setter @Builder
public class UserSessionCardDTO {
    private Long bookingId;
    private String therapistName;
    private String slotDate;
    private String slotTime;
    private String sessionLink;
    private String status;
}
