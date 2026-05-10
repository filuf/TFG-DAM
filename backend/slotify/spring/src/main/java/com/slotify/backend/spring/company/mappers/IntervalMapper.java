package com.slotify.backend.spring.company.mappers;

import com.slotify.backend.spring.company.dtos.CreateIntervalResponse;
import com.slotify.backend.spring.company.dtos.IntervalSummary;
import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.models.CompanyIntervalEntity;
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

    public IntervalSummary toIntervalSummary(CompanyIntervalEntity intervalEntity) {
        IntervalSummary summary = new IntervalSummary();
        summary.setIntervalId(intervalEntity.getIntervalId());
        summary.setStartDateTime(intervalEntity.getStartDatetime());
        summary.setEndDateTime(intervalEntity.getEndDatetime());
        summary.setCreatedAt(intervalEntity.getCreatedAt());
        summary.setMaxConcurrentServices(intervalEntity.getMaxConcurrentServices());
        return summary;
    }
}
