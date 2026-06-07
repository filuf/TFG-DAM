package com.slotify.backend.spring.company.useCases;

import com.slotify.backend.spring.company.dtos.SearchCompaniesResponse;
import com.slotify.backend.spring.company.services.CompanySearchService;
import com.slotify.backend.spring.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchCompaniesUseCaseImpl implements SearchCompaniesUseCase {

    private final CompanySearchService companySearchService;
    private final S3Service s3Service;

    @Override
    public Page<SearchCompaniesResponse> search(String query, Pageable pageable) {
        return this.companySearchService
                .searchCompany(query, pageable)
                .map(doc -> SearchCompaniesResponse.from(doc, this.s3Service.getTemporalUrl(doc.getS3ImageKey())));
    }
}
