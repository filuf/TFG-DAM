package com.slotify.backend.spring.company.useCases;

import com.slotify.backend.spring.company.components.CompanyOwnershipValidator;
import com.slotify.backend.spring.company.components.IntervalOverlapValidator;
import com.slotify.backend.spring.company.components.IntervalTimeValidator;
import com.slotify.backend.spring.company.dtos.UpdateIntervalResponse;
import com.slotify.backend.spring.company.mappers.IntervalMapper;
import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.models.CompanyIntervalEntity;
import com.slotify.backend.spring.company.services.IntervalService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateIntervalUseCaseImpl implements UpdateIntervalUseCase {

    private final IntervalService intervalService;
    private final IntervalTimeValidator intervalTimeValidator;
    private final IntervalOverlapValidator intervalOverlapValidator;
    private final CompanyOwnershipValidator companyOwnershipValidator;
    private final IntervalMapper intervalMapper;

    @Override
    @Transactional
    public UpdateIntervalResponse updateInterval(UUID companyId, UUID intervalId, Integer maxConcurrentService, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        this.intervalTimeValidator.validate(startDateTime, endDateTime);

        CompanyIntervalEntity existingInterval = this.intervalService.findIntervalById(intervalId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe un intervalo en la base de datos con el id: " + intervalId
                ));

        CompanyEntity companyEntity = existingInterval.getCompany();

        this.companyOwnershipValidator.verify(companyId, companyEntity);

        this.intervalOverlapValidator.checkOverlap(startDateTime, endDateTime, companyEntity, intervalId);

        existingInterval.setMaxConcurrentServices(maxConcurrentService);
        existingInterval.setStartDatetime(startDateTime);
        existingInterval.setEndDatetime(endDateTime);
        existingInterval.setCreatedAt(LocalDateTime.now());

        return this.intervalMapper.toUpdateIntervalResponse(existingInterval);
    }
}
