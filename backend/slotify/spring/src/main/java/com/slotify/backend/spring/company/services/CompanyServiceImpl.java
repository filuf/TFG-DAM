package com.slotify.backend.spring.company.services;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    @Override
    public CompanyEntity saveCompany(CompanyEntity companyEntity) {
        return this.companyRepository.save(companyEntity);
    }
}
