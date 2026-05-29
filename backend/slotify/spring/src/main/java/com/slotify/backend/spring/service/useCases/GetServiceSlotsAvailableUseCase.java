package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.reserve.dtos.TimeIntervalDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface GetServiceSlotsAvailableUseCase {
    List<TimeIntervalDTO> getServiceSlotsAvailable(UUID serviceId, LocalDate date);
}
