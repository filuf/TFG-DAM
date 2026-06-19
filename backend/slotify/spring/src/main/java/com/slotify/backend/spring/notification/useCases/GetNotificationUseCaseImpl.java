package com.slotify.backend.spring.notification.useCases;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.notification.components.fetch.NotificationFetchComponentsFactory;
import com.slotify.backend.spring.notification.dtos.NotificationSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetNotificationUseCaseImpl implements GetNotificationUseCase {

    private final NotificationFetchComponentsFactory fetchComponentsFactory;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "notifications", allEntries = true),
            @CacheEvict(value = "notifications-unread-count", allEntries = true)
    })
    public NotificationSummary getNotification(UUID notificationId, UUID accountId, AccountType accountType) {
        return fetchComponentsFactory.forAccountType(accountType)
                .fetcher()
                .fetchById(notificationId, accountId, accountType);
    }
}
