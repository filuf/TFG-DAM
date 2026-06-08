package com.slotify.backend.spring.company.components;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.models.CompanyIntervalEntity;
import com.slotify.backend.spring.company.services.IntervalService;
import com.slotify.backend.spring.exceptions.IntervalValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IntervalOverlapValidator {

    private final IntervalService intervalService;

    public void checkOverlap(LocalDateTime startDateTime, LocalDateTime endDateTime, CompanyEntity companyEntity) {
        checkOverlap(startDateTime, endDateTime, companyEntity, null);
    }

    public void checkOverlap(LocalDateTime startDateTime, LocalDateTime endDateTime, CompanyEntity companyEntity, UUID excludeIntervalId) {
        List<CompanyIntervalEntity> intervals = this.intervalService.findIntervalsByCompanyId(companyEntity.getUserId());

        intervals.stream()
                .filter(interval -> excludeIntervalId == null || !interval.getIntervalId().equals(excludeIntervalId))
                .filter(interval -> startDateTime.isBefore(interval.getEndDatetime()) && interval.getStartDatetime().isBefore(endDateTime))
                .findFirst()
                .ifPresent(overlap -> { throw new IntervalValidationException("El intervalo colisiona con otro existente", overlap, HttpStatus.CONFLICT );});
    }
}
