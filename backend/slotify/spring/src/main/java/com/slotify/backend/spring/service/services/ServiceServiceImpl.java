package com.slotify.backend.spring.service.services;

import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.repositories.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    @Override
    public ServiceEntity saveService(ServiceEntity serviceEntity) {
        log.info("Creando servicio. serviceEntity: {}", serviceEntity);
        return this.serviceRepository.save(serviceEntity);
    }

    @Override
    public Optional<ServiceEntity> findServiceById(UUID serviceId) {
        return this.serviceRepository.findById(serviceId);
    }

    @Override
    public Page<ServiceEntity> findServicesByCompanyId(UUID companyId, Pageable pageable) {
        return this.serviceRepository.findByCompany_UserId(companyId, pageable);
    }
}
