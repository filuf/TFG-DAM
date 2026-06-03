package com.slotify.backend.spring.reserve.components;

import com.slotify.backend.spring.reserve.dtos.TimeIntervalDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Component
public class TimeCompactor {

    public List<TimeIntervalDTO> compactConsecutiveMinutes(List<LocalTime> times) {
        if (times == null || times.isEmpty()) {
            return Collections.emptyList();
        }

        List<LocalTime> sortedTimes = new ArrayList<>(times);
        Collections.sort(sortedTimes);

        List<TimeIntervalDTO> intervals = new ArrayList<>();

        LocalTime start = sortedTimes.get(0).withSecond(0).withNano(0);
        LocalTime end = start;

        for (int i = 1; i < sortedTimes.size(); i++) {
            LocalTime current = sortedTimes.get(i).withSecond(0).withNano(0);

            long gap = ChronoUnit.MINUTES.between(end, current);

            if (gap <= 1) {
                end = current;
            } else {
                intervals.add(new TimeIntervalDTO(start, end));
                start = current;
                end = current;
            }
        }

        intervals.add(new TimeIntervalDTO(start, end));

        return intervals;
    }
}
