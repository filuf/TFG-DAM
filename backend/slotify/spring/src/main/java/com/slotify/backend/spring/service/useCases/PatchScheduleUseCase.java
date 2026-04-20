package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.service.dtos.ScheduleSummary;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalTime;
import java.util.UUID;

public interface PatchScheduleUseCase {
    ScheduleSummary patchSchedule(UUID scheduleId, UUID companyId, JsonNullable<Integer> dayOfWeek,
                                  JsonNullable<LocalTime> startTime, JsonNullable<LocalTime> endTime);
}
