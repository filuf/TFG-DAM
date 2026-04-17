package com.slotify.backend.spring.service.components;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Component
public class ScheduleOverlapValidator {
    private static final int DAY_MINUTES = 1440;
    private static final int WEEK_MINUTES = 7 * DAY_MINUTES;

    public boolean overlaps(
            DayOfWeek day1, LocalTime start1, LocalTime end1,
            DayOfWeek day2, LocalTime start2, LocalTime end2
    ) {

        int start1Min = toWeekMinutes(day1, start1);
        int end1Min = normalizeEnd(start1Min, toWeekMinutes(day1, end1));

        int start2Min = toWeekMinutes(day2, start2);
        int end2Min = normalizeEnd(start2Min, toWeekMinutes(day2, end2));

        // soluciona el wrap-around, servicio domingo 23:00 - lunes 02:00 y viceversa
        return  overlapsInternal(start1Min, end1Min, start2Min, end2Min) ||
                overlapsInternal(start1Min, end1Min, start2Min + WEEK_MINUTES, end2Min + WEEK_MINUTES) ||
                overlapsInternal(start1Min + WEEK_MINUTES, end1Min + WEEK_MINUTES, start2Min, end2Min);
    }

    /**
     * Convierte una hora de un día de la semana a minutos
     *
     * @param day
     * @param time
     * @return
     */
    private int toWeekMinutes(DayOfWeek day, LocalTime time) {
        return (day.getValue() % 7) * DAY_MINUTES
                + time.getHour() * 60
                + time.getMinute();
    }

    /**
     * Si el minuto de fin es anterior al de inicio, finaliza un día después
     *
     * @param start
     * @param end
     * @return
     */
    private int normalizeEnd(int start, int end) {
        return (end <= start) ? end + DAY_MINUTES : end;
    }

    private boolean overlapsInternal(int start1, int end1, int start2, int end2) {
        return start1 < end2 && start2 < end1;
    }
}