package com.slotify.backend.spring.auth.useCases;

import com.slotify.backend.spring.auth.dtos.RegisterCompanyRequest;
import com.slotify.backend.spring.auth.dtos.RegisterCompanyResponse;
import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.auth.mappers.RegisterCompanyMapper;
import com.slotify.backend.spring.auth.services.KeycloakService;
import com.slotify.backend.spring.company.models.CompanyDocument;
import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.services.CompanySearchService;
import com.slotify.backend.spring.company.services.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterCompanyUseCaseImpl implements RegisterCompanyUseCase {

    private final RegisterCompanyMapper registerCompanyMapper;
    private final KeycloakService keycloakService;
    private final CompanyService companyService;
    private final CompanySearchService companySearchService;

    @Override
    @Transactional
    public RegisterCompanyResponse registerCompany(RegisterCompanyRequest request) {

        String companyName = request.getCompanyName();
        String email = request.getEmailAddress();

        UUID userId = this.keycloakService.createUser(companyName, email, request.getPassword(), AccountType.COMPANY);

        CompanyEntity companyEntity = CompanyEntity.builder()
                .userId(userId)
                .companyName(companyName)
                .emailAddress(email)
                .physicalAddress(request.getPhysicalAddress())
                .defaultMaxConcurrentServices(request.getDefaultMaxConcurrentServices())
                .build();
        companyEntity = this.companyService.saveCompany(companyEntity);

        CompanyDocument document = CompanyDocument.builder()
                .companyId(companyEntity.getUserId().toString())
                .companyName(companyEntity.getCompanyName())
                .physicalAddress(companyEntity.getPhysicalAddress())
                .emailAddress(companyEntity.getEmailAddress())
                .phoneNumber(companyEntity.getPhoneNumber())
                .build();
        this.companySearchService.indexCompany(document);

        return this.registerCompanyMapper.toResponse(companyEntity);
    }
}
