package com.slotify.backend.spring.notification.useCases;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.notification.dtos.NotificationSummary;
import com.slotify.backend.spring.notification.mappers.NotificationMapper;
import com.slotify.backend.spring.notification.models.NotificationEntity;
import com.slotify.backend.spring.notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetSentNotificationsUseCaseImpl implements GetSentNotificationsUseCase {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @Override
    @Cacheable(value = "sent-notifications", key = "#accountId + ':' + #accountType + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    public Page<NotificationSummary> getSentNotifications(UUID accountId, AccountType accountType, Pageable pageable) {
        Page<NotificationEntity> sentNotifications = this.notificationService.findSentNotificationsByAccount(accountId, accountType, pageable);
        return sentNotifications.map(this.notificationMapper::toSummary);
    }
}
