package com.slotify.backend.spring.company.dtos;

import com.slotify.backend.spring.company.models.CompanyDocument;
import com.slotify.backend.spring.company.models.CompanyEntity;
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
}
