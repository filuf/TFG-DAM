package com.slotify.backend.spring.service.services;

import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.models.ServiceScheduleEntity;
import com.slotify.backend.spring.service.repositories.ServiceScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceScheduleServiceImpl implements ServiceScheduleService {

    private final ServiceScheduleRepository serviceScheduleRepository;

    @Override
    public List<ServiceScheduleEntity> findByServiceAndDayOfWeekIn(ServiceEntity serviceEntity, List<Integer> days) {
        return this.serviceScheduleRepository.findByServiceAndDayOfWeekIn(serviceEntity, days);
    }

    @Override
    public ServiceScheduleEntity saveSchedule(ServiceScheduleEntity scheduleEntity) {
        return this.serviceScheduleRepository.save(scheduleEntity);
    }
}
