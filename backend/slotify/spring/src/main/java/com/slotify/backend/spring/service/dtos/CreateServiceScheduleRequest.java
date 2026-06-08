package com.slotify.backend.spring.service.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CreateServiceScheduleRequest {

    @NotNull
    private DayOfWeek dayOfWeek;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;
}
