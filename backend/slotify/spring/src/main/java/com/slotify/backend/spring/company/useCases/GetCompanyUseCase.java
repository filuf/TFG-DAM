package com.slotify.backend.spring.company.useCases;

import com.slotify.backend.spring.company.dtos.GetCompanyResponse;
import com.slotify.backend.spring.company.enums.CompanyFetchMode;

import java.util.UUID;

public interface GetCompanyUseCase {


    GetCompanyResponse getCompany(UUID companyId, CompanyFetchMode fetchMode);
}
