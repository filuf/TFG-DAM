package com.slotify.backend.spring.notification.useCases;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.notification.dtos.NotificationSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetNotificationsUseCase {
    Page<NotificationSummary> getNotifications(UUID accountId, AccountType accountType, Pageable pageable);
}
