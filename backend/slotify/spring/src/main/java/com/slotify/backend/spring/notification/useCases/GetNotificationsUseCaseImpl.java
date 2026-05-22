package com.slotify.backend.spring.notification.useCases;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.notification.dtos.NotificationSummary;
import com.slotify.backend.spring.notification.mappers.NotificationMapper;
import com.slotify.backend.spring.notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetNotificationsUseCaseImpl implements GetNotificationsUseCase {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @Override
    public Page<NotificationSummary> getNotifications(UUID accountId, AccountType accountType, Pageable pageable) {
        return this.notificationService
                .findNotificationsByAccount(accountId, accountType, pageable)
                .map(this.notificationMapper::toSummary);
    }
}
