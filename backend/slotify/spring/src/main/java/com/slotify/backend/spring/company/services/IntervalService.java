package com.slotify.backend.spring.company.services;

import com.slotify.backend.spring.company.models.CompanyIntervalEntity;

import java.util.List;
import java.util.UUID;

public interface IntervalService {
    CompanyIntervalEntity saveInterval(CompanyIntervalEntity intervalEntity);

    List<CompanyIntervalEntity> findIntervalsByCompanyId(UUID companyId);
}
