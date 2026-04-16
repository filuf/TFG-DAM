package com.slotify.backend.spring.auth.mappers;

import com.slotify.backend.spring.auth.dtos.RegisterCompanyResponse;
import com.slotify.backend.spring.company.models.CompanyEntity;
import org.springframework.stereotype.Component;

@Component
public class RegisterCompanyMapper {

    public RegisterCompanyResponse toResponse(CompanyEntity companyEntity) {
        return RegisterCompanyResponse.builder()
                .userId(companyEntity.getUserId())
                .username(companyEntity.getCompanyName())
                .email(companyEntity.getEmailAddress())
                .physicalAddress(companyEntity.getPhysicalAddress())
                .build();
    }
}
