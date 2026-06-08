package com.slotify.backend.spring.notification.components.fetch;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.notification.dtos.NotificationSummary;

import java.util.UUID;

public interface NotificationFetcher {

    NotificationSummary fetchById(UUID notificationId, UUID accountId, AccountType accountType);
}
