package com.slotify.backend.spring.notification.services;

import com.slotify.backend.spring.notification.models.NotificationEntity;
import com.slotify.backend.spring.notification.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    @Override
    public NotificationEntity saveNotification(NotificationEntity notificationEntity) {
        return this.notificationRepository.save(notificationEntity);
    }
}
