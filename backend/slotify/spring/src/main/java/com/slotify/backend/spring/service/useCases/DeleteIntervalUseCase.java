package com.slotify.backend.spring.service.useCases;

import java.util.UUID;

public interface DeleteIntervalUseCase {
    void deleteInterval(UUID companyId, UUID intervalId);
}