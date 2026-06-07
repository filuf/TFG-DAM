package com.slotify.backend.spring.company.useCases;

import com.slotify.backend.spring.company.dtos.SearchCompaniesResponse;
import com.slotify.backend.spring.company.mappers.CompanyMapper;
import com.slotify.backend.spring.company.models.CompanyDocument;
import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.services.CompanySearchService;
import com.slotify.backend.spring.company.services.CompanyService;
import com.slotify.backend.spring.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchCompaniesUseCaseImpl implements SearchCompaniesUseCase {

    private final CompanySearchService companySearchService;
    private final CompanyService companyService;
    private final CompanyMapper companyMapper;
    private final S3Service s3Service;

    @Override
    public Page<SearchCompaniesResponse> search(String query, Pageable pageable) {
        Page<CompanyDocument> companyDocuments = this.companySearchService
                .searchCompany(query, pageable);

        List<UUID> ids = companyDocuments.stream()
                .map(CompanyDocument::getCompanyId)
                .map(UUID::fromString)
                .toList();

        List<CompanyEntity> companies = this.companyService.findCompaniesById(ids);

        Map<UUID, CompanyEntity> companiesById = companies.stream()
                .collect(Collectors.toMap(
                        CompanyEntity::getUserId,
                        Function.identity()
                ));

        return companyDocuments.map(companyDocument -> {
            CompanyEntity company = companiesById.get(
                    UUID.fromString(companyDocument.getCompanyId())
            );

            if (company == null) {
                return null;
            }

            String s3ImageUrl = this.s3Service.getTemporalUrl(company.getS3ImageKey());

            return this.companyMapper.toSearchCompaniesResponse(company, s3ImageUrl);
        });

    }
}
