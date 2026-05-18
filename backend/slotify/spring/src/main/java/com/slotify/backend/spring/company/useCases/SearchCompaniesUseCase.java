package com.slotify.backend.spring.company.useCases;

import com.slotify.backend.spring.company.dtos.SearchCompaniesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchCompaniesUseCase {
    Page<SearchCompaniesResponse> search(String query, Pageable pageable);
}
