package com.slotify.backend.spring.notification.mappers;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.notification.dtos.NotificationSummary;
import com.slotify.backend.spring.notification.models.NotificationEntity;
import com.slotify.backend.spring.user.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationMapper {

    public NotificationSummary toSummary(NotificationEntity notificationEntity) {
        NotificationSummary.NotificationSummaryBuilder builder = NotificationSummary.builder()
                .notificationId(notificationEntity.getNotificationId())
                .sender(notificationEntity.getNotificationSender())
                .content(notificationEntity.getTextContent())
                .createdAt(notificationEntity.getCreatedAt())
                .isRead(notificationEntity.isRead());

        switch (notificationEntity.getNotificationSender()) {
            case COMPANY -> {
                CompanyEntity company = notificationEntity.getCompany();
                builder.senderId(company.getUserId())
                        .senderName(company.getCompanyName())
                        .s3SenderImageKey(company.getS3ImageKey());
            }
            case USER -> {
                UserEntity user = notificationEntity.getUser();
                builder.senderId(user.getUserId())
                        .senderName(user.getUsername())
                        .s3SenderImageKey(user.getS3ImageKey());
            }
            case SYSTEM -> builder.senderId(null)
                    .senderName("Slotify")
                    .s3SenderImageKey(null);
        }

        return builder.build();
    }
}
