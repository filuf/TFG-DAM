package com.slotify.backend.spring.company.useCases;

import java.util.UUID;

public interface DeleteIntervalUseCase {

    void deleteInterval(UUID companyId, UUID intervalId);
}
