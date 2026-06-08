package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.service.dtos.ServiceSummary;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface PatchServiceUseCase {
    ServiceSummary patchService(
            UUID serviceId,
            UUID companyId,
            MultipartFile file,
            JsonNullable<String> serviceName,
            JsonNullable<Integer> minutesDuration,
            JsonNullable<Integer> priceCent,
            JsonNullable<String> description
    ) throws IOException;
}
