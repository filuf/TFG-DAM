package com.slotify.backend.spring.reserve.useCases;

import com.slotify.backend.spring.reserve.dtos.CreateReserveResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateReserveUseCase {
    CreateReserveResponse createReserve(UUID userId, UUID serviceId, LocalDateTime dateTimeReserve);
}
