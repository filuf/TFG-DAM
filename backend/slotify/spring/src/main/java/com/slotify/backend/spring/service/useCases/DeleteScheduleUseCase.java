package com.slotify.backend.spring.service.useCases;

import java.util.UUID;

public interface DeleteScheduleUseCase {


    void deleteSchedule(UUID scheduleId, UUID companyId);
}
