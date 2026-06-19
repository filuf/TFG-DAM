package com.slotify.backend.spring.notification.services;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.notification.models.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface NotificationService {

    NotificationEntity saveNotification(NotificationEntity notificationEntity);

    Optional<NotificationEntity> findNotificationById(UUID notificationId);

    Optional<NotificationEntity> findNotificationByIdAndAccountType(UUID notificationId, AccountType accountType, UUID accountId);

    long countUnreadNotifications(UUID accountId, AccountType accountType);

    Page<NotificationEntity> findNotificationsByAccount(UUID accountId, AccountType accountType, Pageable pageable);
}
