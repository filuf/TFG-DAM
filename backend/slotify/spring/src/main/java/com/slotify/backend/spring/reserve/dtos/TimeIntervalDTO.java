package com.slotify.backend.spring.reserve.dtos;

import java.time.LocalTime;

public record TimeIntervalDTO(LocalTime startTime, LocalTime endTime) {

    public TimeIntervalDTO {
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("El startTime no puede ser posterior al endTime");
        }
    }

    /**
     * Verifica si una hora específica cae dentro de este intervalo.
     */
    public boolean contains(LocalTime time) {
        if (time == null) return false;
        return !time.isBefore(startTime) && !time.isAfter(endTime);
    }
}
