package com.slotify.backend.spring.company.useCases;

import com.slotify.backend.spring.company.dtos.GetCompanyResponse;
import com.slotify.backend.spring.company.enums.CompanyFetchMode;
import com.slotify.backend.spring.company.mappers.CompanyMapper;
import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.services.CompanyService;
import com.slotify.backend.spring.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GetCompanyUseCaseImpl implements GetCompanyUseCase {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper;
    private final S3Service s3Service;

    @Override
    public GetCompanyResponse getCompany(UUID companyId, CompanyFetchMode fetchMode) {
        CompanyEntity company = this.companyService.findCompanyByIdAndFetchMode(companyId, fetchMode)
                .orElseThrow(() -> new EntityNotFoundException("No existe una empresa en la base de datos con el id: " + companyId));

        String s3ImageUrl = this.s3Service.getTemporalUrl(company.getS3ImageKey());

        return this.companyMapper.toGetCompanyResponse(company, s3ImageUrl, fetchMode);
    }
}
