package com.slotify.backend.spring.service.services;

import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.models.ServiceScheduleEntity;

import java.util.List;

public interface ServiceScheduleService {
    List<ServiceScheduleEntity> findByServiceAndDayOfWeekIn(ServiceEntity serviceEntity, List<Integer> days);

    ServiceScheduleEntity saveSchedule(ServiceScheduleEntity scheduleEntity);
}
