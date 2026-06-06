package com.slotify.backend.spring.company.useCases;

import com.slotify.backend.spring.company.dtos.UpdateIntervalResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateIntervalUseCase {
    UpdateIntervalResponse updateInterval(UUID companyId, UUID intervalId, Integer maxConcurrentService, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
