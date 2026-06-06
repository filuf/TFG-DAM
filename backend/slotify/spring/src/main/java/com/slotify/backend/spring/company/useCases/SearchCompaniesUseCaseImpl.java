package com.slotify.backend.spring.company.useCases;

import com.slotify.backend.spring.company.dtos.SearchCompaniesResponse;
import com.slotify.backend.spring.company.services.CompanySearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchCompaniesUseCaseImpl implements SearchCompaniesUseCase {

    private final CompanySearchService companySearchService;

    @Override
    public Page<SearchCompaniesResponse> search(String query, Pageable pageable) {
        return this.companySearchService
                .searchCompany(query, pageable)
                .map(SearchCompaniesResponse::from);
    }
}
