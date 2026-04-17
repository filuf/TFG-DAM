package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.service.dtos.CreateServiceScheduleResponse;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public interface CreateServiceScheduleUseCase {
    CreateServiceScheduleResponse createSchedule(UUID companyId, UUID serviceId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime);
}
