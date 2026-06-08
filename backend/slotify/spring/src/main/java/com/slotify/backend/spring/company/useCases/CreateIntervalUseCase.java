package com.slotify.backend.spring.company.useCases;

import com.slotify.backend.spring.company.dtos.CreateIntervalResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateIntervalUseCase {
    CreateIntervalResponse createInterval(UUID companyId, Integer maxConcurrenteService, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
