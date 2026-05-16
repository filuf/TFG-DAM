package com.slotify.backend.spring.notification.components.fetch.impl;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.notification.components.fetch.NotificationFetcher;
import com.slotify.backend.spring.notification.dtos.NotificationSummary;
import com.slotify.backend.spring.notification.mappers.NotificationMapper;
import com.slotify.backend.spring.notification.models.NotificationEntity;
import com.slotify.backend.spring.notification.services.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyNotificationFetcher implements NotificationFetcher {
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;
    @Override
    public NotificationSummary fetchById(UUID notificationId, UUID accountId, AccountType accountType) {
        NotificationEntity notificationEntity = this.notificationService.findNotificationByIdAndAccountType(notificationId, accountType, accountId)
                .orElseThrow(() -> new EntityNotFoundException("No existe en la base de datos una notificacion con el id: " + notificationId));

        if (accountType.matches(notificationEntity.getNotificationSender())) {
            throw new AccessDeniedException("No posees los permisos necesarios para ver este recurso");
        }

        if (!notificationEntity.getCompany().getUserId().equals(accountId)) {
            throw new AccessDeniedException("No posees los permisos necesarios para ver este recurso");
        }

        notificationEntity.markReadIfPossible(accountType);

        return this.notificationMapper.toSummary(notificationEntity);
    }
}
