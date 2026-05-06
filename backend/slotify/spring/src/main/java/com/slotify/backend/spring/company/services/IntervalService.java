package com.slotify.backend.spring.company.services;

import com.slotify.backend.spring.company.models.CompanyIntervalEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IntervalService {
    CompanyIntervalEntity saveInterval(CompanyIntervalEntity intervalEntity);


    List<CompanyIntervalEntity> findIntervalsByCompanyId(UUID companyId);

    Optional<CompanyIntervalEntity> findIntervalById(UUID intervalId);

    void deleteInterval(CompanyIntervalEntity intervalEntity);

    Optional<CompanyIntervalEntity> findIntervalByCompanyIdAndStartDateTime(UUID companyId, LocalDateTime dateTime);

    List<CompanyIntervalEntity> findIntervalByCompanyIdAndBetweenDatesTime(UUID companyId, LocalDateTime reserveStartDateTime, LocalDateTime reserveEndDateTime);
}
