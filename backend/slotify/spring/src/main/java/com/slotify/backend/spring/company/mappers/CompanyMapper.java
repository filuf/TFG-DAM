package com.slotify.backend.spring.company.mappers;

import com.slotify.backend.spring.company.dtos.GetCompanyResponse;
import com.slotify.backend.spring.company.dtos.PatchCompanyResponse;
import com.slotify.backend.spring.company.dtos.SearchCompaniesResponse;
import com.slotify.backend.spring.company.enums.CompanyFetchMode;
import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.service.dtos.ServiceSummary;
import com.slotify.backend.spring.service.mappers.ServiceMapper;
import com.slotify.backend.spring.service.models.ServiceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompanyMapper {

    private final ServiceMapper serviceMapper;

    public GetCompanyResponse toGetCompanyResponse(CompanyEntity company, String s3ImageUrl, CompanyFetchMode fetchMode) {
        GetCompanyResponse.GetCompanyResponseBuilder builder = GetCompanyResponse.builder()
                .companyId(company.getUserId())
                .defaultMaxConcurrentServices(company.getDefaultMaxConcurrentServices())
                .companyName(company.getCompanyName())
                .phoneNumber(company.getPhoneNumber())
                .emailAddress(company.getEmailAddress())
                .physicalAddress(company.getPhysicalAddress())
                .s3ImageUrl(s3ImageUrl)
                .description(company.getDescription())
                .rattingAvg(company.getRattingAvg());

        switch (fetchMode) {
            case WITH_SERVICES -> {
                List<ServiceEntity> services = company.getServices();
                List<ServiceSummary> servicesSummaries = services.stream()
                        .map(this.serviceMapper::toServiceSummary)
                        .toList();

                builder.services(servicesSummaries);
            }
        }

        return builder.build();
    }

    public PatchCompanyResponse toPatchCompanyResponse(CompanyEntity company, String s3ImageUrl) {
        return PatchCompanyResponse.builder()
                .companyId(company.getUserId())
                .defaultMaxConcurrentServices(company.getDefaultMaxConcurrentServices())
                .companyName(company.getCompanyName())
                .phoneNumber(company.getPhoneNumber())
                .physicalAddress(company.getPhysicalAddress())
                .s3ImageUrl(s3ImageUrl)
                .description(company.getDescription())
                .build();
    }

    public SearchCompaniesResponse toSearchCompaniesResponse(CompanyEntity companyEntity, String s3ImageUrl) {
        return SearchCompaniesResponse.builder()
                .companyId(companyEntity.getUserId())
                .companyName(companyEntity.getCompanyName())
                .physicalAddress(companyEntity.getPhysicalAddress())
                .description(companyEntity.getDescription())
                .rattingAvg(companyEntity.getRattingAvg())
                .s3ImageUrl(s3ImageUrl)
                .build();
    }
}
