package com.slotify.backend.spring.service.util;

import com.slotify.backend.spring.service.models.ServiceScheduleEntity;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;

public class ScheduleFormatter {

    private ScheduleFormatter(){}
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static String format(ServiceScheduleEntity schedule) {
        if (schedule == null) return "";

        return schedule.getDayOfWeek() + " " + schedule.getStartTime().format(TIME_FORMATTER) + " - " + schedule.getEndTime().format(TIME_FORMATTER);
    }
}
