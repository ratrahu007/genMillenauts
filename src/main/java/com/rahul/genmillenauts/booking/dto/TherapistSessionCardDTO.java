package com.rahul.genmillenauts.booking.dto;

import lombok.*;

@Getter @Setter @Builder
public class TherapistSessionCardDTO {
    private Long bookingId;
    private String userName;
    private String slotDate;
    private String slotTime;
    private String sessionLink;
    private String status;
}
