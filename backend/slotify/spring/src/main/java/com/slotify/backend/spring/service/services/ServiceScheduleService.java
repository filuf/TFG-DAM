package com.slotify.backend.spring.service.services;

import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.models.ServiceScheduleEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceScheduleService {
    List<ServiceScheduleEntity> findAllByServiceAndDayOfWeekIn(ServiceEntity serviceEntity, List<Integer> days);

    ServiceScheduleEntity saveSchedule(ServiceScheduleEntity scheduleEntity);

    Optional<ServiceScheduleEntity> findById(UUID scheduleId);
}
