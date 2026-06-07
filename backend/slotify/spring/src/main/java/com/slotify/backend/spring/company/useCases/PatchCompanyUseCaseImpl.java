package com.slotify.backend.spring.company.useCases;

import com.slotify.backend.spring.company.dtos.PatchCompanyResponse;
import com.slotify.backend.spring.company.mappers.CompanyMapper;
import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.services.CompanyService;
import com.slotify.backend.spring.s3.ImageFormatValidator;
import com.slotify.backend.spring.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatchCompanyUseCaseImpl implements PatchCompanyUseCase {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper;
    private final ImageFormatValidator imageFormatValidator;
    private final S3Service s3Service;

    @Override
    @Transactional
    public PatchCompanyResponse patchCompany(
            UUID companyId,
            MultipartFile file,
            JsonNullable<Integer> defaultMaxConcurrentServices,
            JsonNullable<String> companyName,
            JsonNullable<String> phoneNumber,
            JsonNullable<String> physicalAddress,
            JsonNullable<String> description
    ) throws IOException {

        CompanyEntity company = this.companyService.findCompanyById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("No existe una empresa en la base de datos con el id: " + companyId));

        this.patchEntity(file, defaultMaxConcurrentServices, companyName, phoneNumber, physicalAddress, description, company);

        String s3ImageUrl = this.s3Service.getTemporalUrl(company.getS3ImageKey());

        return this.companyMapper.toPatchCompanyResponse(company, s3ImageUrl);
    }

    private void patchEntity(
            MultipartFile file,
            JsonNullable<Integer> defaultMaxConcurrentServices,
            JsonNullable<String> companyName,
            JsonNullable<String> phoneNumber,
            JsonNullable<String> physicalAddress,
            JsonNullable<String> description,
            CompanyEntity company
    ) throws IOException {

        if (this.imageFormatValidator.validateImage(file)) {
            String imageKeyName = this.s3Service.generateFileName(file);
            this.s3Service.uploadFile(imageKeyName, file);

            company.setS3ImageKey(imageKeyName);
        }

        if (defaultMaxConcurrentServices.isPresent()) {
            company.setDefaultMaxConcurrentServices(defaultMaxConcurrentServices.get());
        }

        if (companyName.isPresent()) {
            company.setCompanyName(companyName.get());
        }

        if (phoneNumber.isPresent()) {
            company.setPhoneNumber(phoneNumber.get());
        }

        if (physicalAddress.isPresent()) {
            company.setPhysicalAddress(physicalAddress.get());
        }

        if (description.isPresent()) {
            company.setDescription(description.get());
        }
    }
}
