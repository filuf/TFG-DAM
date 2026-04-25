package com.slotify.backend.spring.company.mappers;

import com.slotify.backend.spring.company.dtos.CreateIntervalResponse;
import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.models.CompanyIntervalEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class IntervalMapper {

    public CompanyIntervalEntity toEntity(Integer maxConcurrenteService, LocalDateTime startDateTime, LocalDateTime endDateTime, CompanyEntity companyEntity) {
        return CompanyIntervalEntity.builder()
                .company(companyEntity)
                .createdAt(LocalDateTime.now())
                .maxConcurrentServices(maxConcurrenteService)
                .startDatetime(startDateTime)
                .endDatetime(endDateTime)
                .build();
    }

    public CreateIntervalResponse toCreateIntervalResponse(CompanyIntervalEntity intervalEntity) {
        return CreateIntervalResponse.builder()
                .intervalId(intervalEntity.getIntervalId())
                .maxConcurrentServices(intervalEntity.getMaxConcurrentServices())
                .startDatetime(intervalEntity.getStartDatetime())
                .endDatetime(intervalEntity.getEndDatetime())
                .build();
    }
}
