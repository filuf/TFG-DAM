package com.slotify.backend.spring.reserve.schedulers;

import com.slotify.backend.spring.notification.models.NotificationEntity;
import com.slotify.backend.spring.notification.models.NotificationSender;
import com.slotify.backend.spring.notification.services.NotificationService;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import com.slotify.backend.spring.reserve.repositories.ReserveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewReminderScheduler {

    private final ReserveRepository reserveRepository;
    private final NotificationService notificationService;
    private final Clock clock;

    @Scheduled(fixedRate = 300_000)
    public void sendReviewNotification() {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime rangeStart = now.minusMinutes(125);
        LocalDateTime rangeEnd   = now.minusMinutes(120);

        List<ReserveEntity> reserves =
                reserveRepository.findByServiceTimeBetweenAndIsCanceledFalse(rangeStart, rangeEnd);

        reserves.forEach(reserve -> {
            NotificationEntity notification = NotificationEntity.builder()
                    .notificationSender(NotificationSender.SYSTEM)
                    .textContent("¿Qué tal tu experiencia? ¡Nos encantaría conocer tu opinión! Deja una reseña sobre tu última reserva.")
                    .createdAt(LocalDateTime.now(clock))
                    .company(reserve.getService().getCompany())
                    .user(reserve.getUser())
                    .build();

            notificationService.saveNotification(notification);
        });
    }
}
