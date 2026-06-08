package com.slotify.backend.spring.service.projections;

import java.time.LocalTime;
import java.util.UUID;

public record ServiceScheduleDTO(
        UUID serviceId,
        Integer serviceMinutesDuration,
        Integer dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {}