package com.slotify.backend.spring.reserve.repositories;

import com.slotify.backend.spring.reserve.models.ReserveEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReserveRepository extends JpaRepository<ReserveEntity, UUID> {

    List<ReserveEntity> findByUser_UserId(UUID userId);


    @EntityGraph(attributePaths = {"service"})
    List<ReserveEntity> findByUser_UserIdAndServiceTimeBetweenAndIsCanceledFalse(
            UUID userId,
            LocalDateTime start,
            LocalDateTime end
    );

    @EntityGraph(attributePaths = {"service"})
    List<ReserveEntity> findByService_Company_UserIdAndServiceTimeBetweenAndIsCanceledFalse(
            UUID userId,
            LocalDateTime start,
            LocalDateTime end
    );

}
