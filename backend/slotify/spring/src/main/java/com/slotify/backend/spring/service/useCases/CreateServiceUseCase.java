package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.service.dtos.CreateServiceResponse;

import java.util.UUID;

public interface CreateServiceUseCase {
    CreateServiceResponse createService(UUID companyId, String serviceName, Integer minutesDuration, Integer priceCent);
}
