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
    private String s3ImageUrl;

    public static SearchCompaniesResponse from(CompanyDocument doc, String s3ImageUrl) {
        return SearchCompaniesResponse.builder()
                .companyId(UUID.fromString(doc.getCompanyId()))
                .companyName(doc.getCompanyName())
                .physicalAddress(doc.getPhysicalAddress())
                .description(doc.getDescription())
                .rattingAvg(doc.getRattingAvg())
                .s3ImageUrl(s3ImageUrl)
                .build();
    }
}
