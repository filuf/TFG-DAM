package com.slotify.backend.spring.company.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class IntervalSummary {

    private UUID intervalId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime createdAt;
    private Integer maxConcurrentServices;
}
