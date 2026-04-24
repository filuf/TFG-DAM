package com.slotify.backend.spring.notification.repositories;

import com.slotify.backend.spring.notification.models.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {
}
