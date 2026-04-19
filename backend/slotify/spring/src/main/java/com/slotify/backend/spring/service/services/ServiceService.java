package com.slotify.backend.spring.service.services;

import com.slotify.backend.spring.service.enums.ServiceFetchMode;
import com.slotify.backend.spring.service.models.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ServiceService {
    ServiceEntity saveService(ServiceEntity serviceEntity);

    Optional<ServiceEntity> findServiceById(UUID serviceId);

    Page<ServiceEntity> findServicesByCompanyId(UUID companyId, Pageable pageable);
}
