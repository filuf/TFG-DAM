package com.slotify.backend.spring.company.services;

import com.slotify.backend.spring.company.models.CompanyDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanySearchService {
    void indexCompany(CompanyDocument document);
    Page<CompanyDocument> searchCompany(String query, Pageable pageable);

    CompanyDocument findCompanyById(String companyId);
}
