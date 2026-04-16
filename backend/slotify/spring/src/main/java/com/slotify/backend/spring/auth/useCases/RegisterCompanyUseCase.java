package com.slotify.backend.spring.auth.useCases;

import com.slotify.backend.spring.auth.dtos.RegisterCompanyRequest;
import com.slotify.backend.spring.auth.dtos.RegisterCompanyResponse;

public interface RegisterCompanyUseCase {
    RegisterCompanyResponse registerCompany(RegisterCompanyRequest registerCompanyRequest);
}
