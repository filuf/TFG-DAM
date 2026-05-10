package com.slotify.backend.spring.company.services;

import com.slotify.backend.spring.company.enums.IntervalFetchMode;
import com.slotify.backend.spring.company.models.CompanyIntervalEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IntervalService {
    CompanyIntervalEntity saveInterval(CompanyIntervalEntity intervalEntity);

    List<CompanyIntervalEntity> findIntervalsByCompanyId(UUID companyId);

    List<CompanyIntervalEntity> findIntervalsByCompanyId(UUID companyId, IntervalFetchMode fetchMode);

    Optional<CompanyIntervalEntity> findIntervalById(UUID intervalId);

    void deleteInterval(UUID intervalId);
}
