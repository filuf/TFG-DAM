package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.services.CompanyService;
import com.slotify.backend.spring.service.dtos.CreateServiceResponse;
import com.slotify.backend.spring.service.dtos.ServiceCreatedEvent;
import com.slotify.backend.spring.service.mappers.ServiceMapper;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.services.ServiceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateServiceUseCaseImpl implements CreateServiceUseCase {

    private final ServiceMapper serviceMapper;
    private final ServiceService serviceService;
    private final CompanyService companyService;
    private final ApplicationEventPublisher publisher;

    @Override
    @Transactional
    public CreateServiceResponse createService(UUID companyId, String serviceName, Integer minutesDuration, Integer priceCent, String description) {

        CompanyEntity companyEntity = this.companyService.findCompanyById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("No existe una empresa en la base de datos con el id: " + companyId));

        ServiceEntity serviceEntity = this.serviceMapper.toEntity(companyEntity, serviceName, minutesDuration, priceCent, description);

        serviceEntity = this.serviceService.saveService(serviceEntity);

        this.publisher.publishEvent(new ServiceCreatedEvent(companyEntity, serviceEntity));

        return this.serviceMapper.toCreateServiceResponse(serviceEntity);
    }
}
