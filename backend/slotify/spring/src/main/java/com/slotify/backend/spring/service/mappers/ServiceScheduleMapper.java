package com.slotify.backend.spring.service.mappers;

import com.slotify.backend.spring.service.dtos.CreateServiceScheduleResponse;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.models.ServiceScheduleEntity;
import com.slotify.backend.spring.service.util.ScheduleFormatter;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Component
public class ServiceScheduleMapper {

    public ServiceScheduleEntity toEntity(
            ServiceEntity serviceEntity, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime
    ) {
        return ServiceScheduleEntity.builder()
                .service(serviceEntity)
                .dayOfWeek(dayOfWeek.getValue())
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    public CreateServiceScheduleResponse toCreateServiceScheduleResponse(
            ServiceScheduleEntity scheduleEntity, DayOfWeek dayOfWeek, ServiceEntity serviceEntity
    ) {
        return CreateServiceScheduleResponse.builder()
                .scheduleId(scheduleEntity.getId())
                .dayOfWeek(dayOfWeek.getValue())
                .serviceName(serviceEntity.getServiceName())
                .interval(ScheduleFormatter.format(scheduleEntity))
                .build();
    }
}
