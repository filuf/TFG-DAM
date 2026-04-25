package com.slotify.backend.spring.company.useCases;

import com.slotify.backend.spring.company.components.IntervalOverlapValidator;
import com.slotify.backend.spring.company.components.IntervalTimeValidator;
import com.slotify.backend.spring.company.dtos.CreateIntervalResponse;
import com.slotify.backend.spring.company.mappers.IntervalMapper;
import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.models.CompanyIntervalEntity;
import com.slotify.backend.spring.company.services.CompanyService;
import com.slotify.backend.spring.company.services.IntervalService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateIntervalUseCaseImpl implements CreateIntervalUseCase {

    private final CompanyService companyService;
    private final IntervalService intervalService;
    private final IntervalTimeValidator intervalTimeValidator;
    private final IntervalOverlapValidator intervalOverlapValidator;
    private final IntervalMapper intervalMapper;

    @Override
    public CreateIntervalResponse createInterval(UUID companyId, Integer maxConcurrenteService, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        this.intervalTimeValidator.validate(startDateTime, endDateTime);

        CompanyEntity companyEntity = this.companyService.findCompanyById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("No existe una empresa en la base de datos con el id: " + companyId));

        this.intervalOverlapValidator.checkOverlap(startDateTime, endDateTime, companyEntity);

        CompanyIntervalEntity intervalEntity = this.intervalService.saveInterval(
                this.intervalMapper.toEntity(maxConcurrenteService, startDateTime, endDateTime, companyEntity)
        );

        return this.intervalMapper.toCreateIntervalResponse(intervalEntity);
    }




}
