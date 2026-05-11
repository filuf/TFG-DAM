package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.service.dtos.ScheduleSummary;
import com.slotify.backend.spring.service.dtos.ServiceSummary;
import com.slotify.backend.spring.service.mappers.ServiceMapper;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.services.ServiceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetServiceSchedulesUseCaseImpl implements GetServiceSchedulesUseCase {

    private final ServiceService serviceService;
    private final ServiceMapper serviceMapper;
    @Override
    public ServiceSummary getServiceSummary(UUID serviceId) {
        ServiceEntity service = this.serviceService.findServiceWithSchedulesById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("No existe un servicio en la base de datos con el id: " + serviceId));

        return this.serviceMapper.toServiceSummary(service);
    }


}
