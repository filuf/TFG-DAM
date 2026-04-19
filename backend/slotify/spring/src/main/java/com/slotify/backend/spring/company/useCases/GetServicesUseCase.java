package com.slotify.backend.spring.company.useCases;

import com.slotify.backend.spring.company.dtos.GetServicesResponse;
import com.slotify.backend.spring.service.enums.ServiceFetchMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetServicesUseCase {
    Page<GetServicesResponse> getServices(UUID companyId, ServiceFetchMode fetchMode, Pageable pageable);
}
