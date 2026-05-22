package com.slotify.backend.spring.notification.useCases;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.notification.dtos.UnreadNotificationCountResponse;

import java.util.UUID;

public interface GetUnreadNotificationCountUseCase {
    UnreadNotificationCountResponse getUnreadCount(UUID accountId, AccountType accountType);
}
