package com.slotify.backend.spring.company.useCases;

import com.slotify.backend.spring.company.dtos.IntervalSummary;
import com.slotify.backend.spring.company.enums.IntervalFetchMode;
import com.slotify.backend.spring.company.mappers.IntervalMapper;
import com.slotify.backend.spring.company.services.IntervalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetIntervalsUseCaseImpl implements GetIntervalsUseCase {

    private final IntervalService intervalService;
    private final IntervalMapper intervalMapper;

    @Override
    public List<IntervalSummary> getIntervals(UUID companyId, IntervalFetchMode fetchMode) {
        return this.intervalService.findIntervalsByCompanyId(companyId, fetchMode)
                .stream()
                .map(this.intervalMapper::toIntervalSummary)
                .toList();
    }
}
