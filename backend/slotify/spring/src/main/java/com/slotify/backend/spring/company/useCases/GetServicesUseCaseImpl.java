package com.slotify.backend.spring.company.useCases;

import com.slotify.backend.spring.company.dtos.GetServicesResponse;
import com.slotify.backend.spring.service.enums.ServiceFetchMode;
import com.slotify.backend.spring.service.mappers.ServiceMapper;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.services.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetServicesUseCaseImpl implements GetServicesUseCase {

    private final ServiceService serviceService;
    private final ServiceMapper serviceMapper;
    @Override
    public Page<GetServicesResponse> getServices(UUID companyId, ServiceFetchMode fetchMode, Pageable pageable) {

        Page<ServiceEntity> servicesByCompanyId = this.serviceService.findServicesByCompanyId(companyId, pageable);
        return servicesByCompanyId.map(service -> this.serviceMapper.toGetServicesResponse(service, fetchMode));
    }
}
