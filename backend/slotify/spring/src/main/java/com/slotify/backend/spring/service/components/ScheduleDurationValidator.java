package com.slotify.backend.spring.service.components;

import com.slotify.backend.spring.exceptions.ScheduleValidationException;
import com.slotify.backend.spring.service.enums.ScheduleLimits;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;

@Component
public class ScheduleDurationValidator {

    public void validate(LocalTime startTime, LocalTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        if (duration.isNegative()) {
            duration = duration.plusDays(1);
        }
        int minutesDuration = (int) duration.toMinutes();

        if (minutesDuration > ScheduleLimits.MAX_MINUTES_DURATION.getValue()) {
            throw new ScheduleValidationException(
                    "La duración del servicio no puede durar más de " +
                    ScheduleLimits.MAX_MINUTES_DURATION.getValue() + " minutos", HttpStatus.BAD_REQUEST);
        }
        if (minutesDuration < ScheduleLimits.MIN_MINUTES_DURATION.getValue()) {
            throw new ScheduleValidationException(
                    "La duración del servicio no puede durar menos de " +
                    ScheduleLimits.MIN_MINUTES_DURATION.getValue() + " minutos", HttpStatus.BAD_REQUEST);
        }
    }
}
