package com.slotify.backend.spring.company.services;

import com.slotify.backend.spring.company.models.CompanyEntity;

import java.util.Optional;
import java.util.UUID;

public interface CompanyService {

    CompanyEntity saveCompany(CompanyEntity companyEntity);

    Optional<CompanyEntity> findCompanyById(UUID companyId);
}
