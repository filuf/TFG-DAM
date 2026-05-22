package com.slotify.backend.spring.notification.services;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.notification.models.NotificationEntity;
import com.slotify.backend.spring.notification.repositories.NotificationRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    @Override
    public NotificationEntity saveNotification(NotificationEntity notificationEntity) {
        return this.notificationRepository.save(notificationEntity);
    }

    @Override
    public Optional<NotificationEntity> findNotificationById(UUID notificationId) {
        return this.notificationRepository.findById(notificationId);
    }

    @Override
    public Optional<NotificationEntity> findNotificationByIdAndAccountType(UUID notificationId, AccountType accountType, UUID accountId) {

        Specification<NotificationEntity> spec = ( (root, query, cb) -> {

            Predicate notificationPred = cb.equal(root.get("notificationId"), notificationId);
            root.fetch("company");
            root.fetch("user");

            Predicate userPred = switch (accountType) {
                case USER -> cb.equal(root.get("user").get("userId"), accountId);
                case COMPANY -> cb.equal(root.get("company").get("userId"), accountId);
            };

            return cb.and(notificationPred, userPred);
        });

        return this.notificationRepository.findOne(spec);
    }

    @Override
    public long countUnreadNotifications(UUID accountId, AccountType accountType) {

        Specification<NotificationEntity> spec = (root, query, cb) -> {

            Predicate isUnreadPred = cb.isFalse(root.get("isRead"));

            Predicate accountPred = switch (accountType) {
                case USER -> cb.equal(root.get("user").get("userId"), accountId);
                case COMPANY -> cb.equal(root.get("company").get("userId"), accountId);
            };

            return cb.and(isUnreadPred, accountPred);
        };

        return this.notificationRepository.count(spec);
    }
}
