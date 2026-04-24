package com.slotify.backend.spring.notification.models;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.user.models.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class NotificationEntity {

    @Id
    @Column(name = "notification_id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private UUID notificationId;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "notification_sender",
            nullable = false,
            columnDefinition = "reserves.notification_sender"
    )
    private NotificationSender notificationSender;

    @Column(name = "text_content", nullable = false, columnDefinition = "TEXT")
    private String textContent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companies_id", nullable = false)
    private CompanyEntity company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
