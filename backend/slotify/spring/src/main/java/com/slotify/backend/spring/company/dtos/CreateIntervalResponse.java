package com.slotify.backend.spring.company.dtos;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@ToString
public class CreateIntervalResponse {

    private UUID intervalId;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private Integer maxConcurrentServices;

}
