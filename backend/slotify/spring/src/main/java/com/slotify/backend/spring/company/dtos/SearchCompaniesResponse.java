package com.slotify.backend.spring.company.dtos;

import com.slotify.backend.spring.company.models.CompanyDocument;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class SearchCompaniesResponse {
    private UUID companyId;
    private String companyName;
    private String physicalAddress;
    private String description;
    private Short rattingAvg;

    public static SearchCompaniesResponse from(CompanyDocument doc) {
        return SearchCompaniesResponse.builder()
                .companyId(UUID.fromString(doc.getCompanyId()))
                .companyName(doc.getCompanyName())
                .physicalAddress(doc.getPhysicalAddress())
                .description(doc.getDescription())
                .rattingAvg(doc.getRattingAvg())
                .build();
    }
}
