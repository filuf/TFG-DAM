package com.slotify.backend.spring.notification.dtos;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UnreadNotificationCountResponse {
    private long count;
}
