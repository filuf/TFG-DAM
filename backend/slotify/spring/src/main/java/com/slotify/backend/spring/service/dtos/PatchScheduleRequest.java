package com.slotify.backend.spring.service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class PatchScheduleRequest {
    private JsonNullable<Integer> dayOfWeek = JsonNullable.undefined();
    private JsonNullable<LocalTime> startTime = JsonNullable.undefined();
    private JsonNullable<LocalTime> endTime = JsonNullable.undefined();
}
