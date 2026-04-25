package com.slotify.backend.spring.company.services;

import com.slotify.backend.spring.company.models.CompanyIntervalEntity;
import com.slotify.backend.spring.company.repositories.IntervalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntervalServiceImpl implements IntervalService {

    private final IntervalRepository intervalRepository;

    @Override
    public CompanyIntervalEntity saveInterval(CompanyIntervalEntity intervalEntity) {
        return this.intervalRepository.save(intervalEntity);
    }

    @Override
    public List<CompanyIntervalEntity> findIntervalsByCompanyId(UUID companyId) {
        return this.intervalRepository.findByCompany_UserId(companyId);
    }
}
