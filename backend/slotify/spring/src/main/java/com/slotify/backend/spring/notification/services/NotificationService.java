package com.slotify.backend.spring.notification.services;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.notification.models.NotificationEntity;

import java.util.Optional;
import java.util.UUID;

public interface NotificationService {

    NotificationEntity saveNotification(NotificationEntity notificationEntity);

    Optional<NotificationEntity> findNotificationById(UUID notificationId);

    Optional<NotificationEntity> findNotificationByIdAndAccountType(UUID notificationId, AccountType accountType, UUID accountId);
}
