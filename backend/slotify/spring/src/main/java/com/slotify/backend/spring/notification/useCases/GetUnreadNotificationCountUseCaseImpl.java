package com.slotify.backend.spring.notification.useCases;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.notification.dtos.UnreadNotificationCountResponse;
import com.slotify.backend.spring.notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUnreadNotificationCountUseCaseImpl implements GetUnreadNotificationCountUseCase {

    private final NotificationService notificationService;

    @Override
    public UnreadNotificationCountResponse getUnreadCount(UUID accountId, AccountType accountType) {
        long count = this.notificationService.countUnreadNotifications(accountId, accountType);
        return UnreadNotificationCountResponse.builder()
                .count(count)
                .build();
    }
}
