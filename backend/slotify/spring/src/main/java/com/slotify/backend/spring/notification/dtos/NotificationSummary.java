package com.slotify.backend.spring.notification.dtos;


import com.slotify.backend.spring.notification.models.NotificationSender;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@ToString
public class NotificationSummary {

    UUID notificationId;
    NotificationSender sender;
    String content;
    LocalDateTime createdAt;
    boolean isRead;

    UUID senderId;
    String senderName;
    String s3SenderImageKey;

}
