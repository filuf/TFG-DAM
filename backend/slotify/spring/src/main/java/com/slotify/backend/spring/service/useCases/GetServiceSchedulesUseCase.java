package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.service.dtos.ServiceSummary;

import java.util.UUID;

public interface GetServiceSchedulesUseCase {

    ServiceSummary getServiceSummary(UUID serviceId);
}
