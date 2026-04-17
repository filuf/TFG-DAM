package com.slotify.backend.spring.service.services;

import com.slotify.backend.spring.service.models.ServiceEntity;

import java.util.Optional;
import java.util.UUID;

public interface ServiceService {
    ServiceEntity saveService(ServiceEntity serviceEntity);

    Optional<ServiceEntity> findServiceById(UUID serviceId);
}
