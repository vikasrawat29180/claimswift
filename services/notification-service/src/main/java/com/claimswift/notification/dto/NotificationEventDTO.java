package com.claimswift.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationEventDTO {

    private Long userId;
    private Long claimId;
    private String title;
    private String message;
}