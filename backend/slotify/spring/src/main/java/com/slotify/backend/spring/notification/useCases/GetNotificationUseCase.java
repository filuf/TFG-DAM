package com.slotify.backend.spring.notification.useCases;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.notification.dtos.NotificationSummary;

import java.util.UUID;

public interface GetNotificationUseCase {

    NotificationSummary getNotification(UUID notificationId, UUID accountId, AccountType accountType);
}
