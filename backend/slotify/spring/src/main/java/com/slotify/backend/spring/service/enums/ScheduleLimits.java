package com.slotify.backend.spring.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum ScheduleLimits {
    MAX_MINUTES_DURATION(480),
    MIN_MINUTES_DURATION(5);

    private final int value;
}
