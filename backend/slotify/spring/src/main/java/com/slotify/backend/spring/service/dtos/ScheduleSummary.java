package com.slotify.backend.spring.service.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalTime;
import java.util.UUID;

@Builder
@Getter
@ToString
public class ScheduleSummary {

    private UUID id;
    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

}
