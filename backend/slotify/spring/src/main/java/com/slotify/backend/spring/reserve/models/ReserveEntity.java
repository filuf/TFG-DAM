package com.slotify.backend.spring.reserve.models;

import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.user.models.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reserves")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"user", "service"})
public class ReserveEntity {

    @Id
    @Column(name = "reserve_id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reserveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @Column(name = "service_time", nullable = false)
    private LocalDateTime serviceTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_canceled", nullable = false)
    private boolean isCanceled;

    public void cancelReserve() {

        if (isCanceled) {
            throw new IllegalStateException("No se puede cancelar una reserva ya cancelada");
        }
        isCanceled = true;
    }
}
