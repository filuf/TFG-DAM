package com.slotify.backend.spring.notification.controllers;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.notification.dtos.NotificationSummary;
import com.slotify.backend.spring.notification.dtos.UnreadNotificationCountResponse;
import com.slotify.backend.spring.notification.useCases.GetNotificationUseCase;
import com.slotify.backend.spring.notification.useCases.GetUnreadNotificationCountUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final GetNotificationUseCase getNotificationUseCase;
    private final GetUnreadNotificationCountUseCase getUnreadNotificationCountUseCase;

    @GetMapping("{notificationId}")
    @PreAuthorize("hasAnyRole('USER','COMPANY')")
    public ResponseEntity<NotificationSummary> getNotification(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        AccountType accountType = AccountType.fromType(jwt.getClaim("account-type"));

        NotificationSummary notification = this.getNotificationUseCase.getNotification(
                notificationId,
                UUID.fromString(jwt.getSubject()),
                accountType
        );

        return ResponseEntity.ok(notification);
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('USER','COMPANY')")
    public ResponseEntity<UnreadNotificationCountResponse> getUnreadCount(
            @AuthenticationPrincipal Jwt jwt
    ) {
        AccountType accountType = AccountType.fromType(jwt.getClaim("account-type"));

        UnreadNotificationCountResponse response = this.getUnreadNotificationCountUseCase.getUnreadCount(
                UUID.fromString(jwt.getSubject()),
                accountType
        );

        return ResponseEntity.ok(response);
    }

}
