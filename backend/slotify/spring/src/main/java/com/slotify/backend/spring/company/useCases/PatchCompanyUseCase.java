package com.slotify.backend.spring.company.useCases;

import com.slotify.backend.spring.company.dtos.PatchCompanyResponse;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface PatchCompanyUseCase {

    PatchCompanyResponse patchCompany(
            UUID companyId,
            MultipartFile file,
            JsonNullable<Integer> defaultMaxConcurrentServices,
            JsonNullable<String> phoneNumber,
            JsonNullable<String> physicalAddress,
            JsonNullable<String> description
    ) throws IOException;
}
