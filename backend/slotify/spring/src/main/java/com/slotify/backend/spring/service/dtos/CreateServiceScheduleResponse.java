package com.slotify.backend.spring.service.dtos;

import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.UUID;

@Builder
@Getter
public class CreateServiceScheduleResponse {

    private UUID scheduleId;
    private String serviceName;
    private Integer dayOfWeek;
    private String interval;
}
