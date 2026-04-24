package com.slotify.backend.spring.notification.services;

import com.slotify.backend.spring.notification.models.NotificationEntity;

public interface NotificationService {

    NotificationEntity saveNotification(NotificationEntity notificationEntity);
}
