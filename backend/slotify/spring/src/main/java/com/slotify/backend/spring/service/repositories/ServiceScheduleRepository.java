package com.slotify.backend.spring.service.repositories;

import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.models.ServiceScheduleEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.UUID;

public interface ServiceScheduleRepository extends JpaRepository<ServiceScheduleEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<ServiceScheduleEntity> findByServiceAndDayOfWeekIn(ServiceEntity service, List<Integer> days);
}
