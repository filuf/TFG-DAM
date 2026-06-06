package com.slotify.backend.spring.company.services;

import com.slotify.backend.spring.company.enums.CompanyFetchMode;
import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    @Override
    public CompanyEntity saveCompany(CompanyEntity companyEntity) {
        return this.companyRepository.save(companyEntity);
    }

    @Override
    public Optional<CompanyEntity> findCompanyById(UUID companyId) {
        return this.companyRepository.findById(companyId);
    }

    @Override
    public Optional<CompanyEntity> findCompanyByIdAndFetchMode(UUID companyId, CompanyFetchMode fetchMode) {
        Specification<CompanyEntity> spec = ((root, query, cb) ->
            switch (fetchMode) {
                case BASIC -> cb.equal(root.get("userId"), companyId);
                case WITH_SERVICES -> {
                    root.fetch("services");
                    yield cb.equal(root.get("userId"), companyId);
                }
            }
        );

        return this.companyRepository.findOne(spec);
    }


}
