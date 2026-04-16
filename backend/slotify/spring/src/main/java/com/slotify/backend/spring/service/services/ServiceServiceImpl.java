package com.slotify.backend.spring.service.services;

import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.repositories.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
