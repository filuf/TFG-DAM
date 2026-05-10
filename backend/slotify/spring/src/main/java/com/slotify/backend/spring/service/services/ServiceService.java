package com.slotify.backend.spring.service.services;

import com.slotify.backend.spring.service.projections.ServiceScheduleDTO;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.projections.ServiceDurationRange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceService {
    ServiceEntity saveService(ServiceEntity serviceEntity);

    Optional<ServiceEntity> findServiceById(UUID serviceId);
    Optional<ServiceEntity> findServiceByIdWithCompany(UUID serviceId);

    Page<ServiceEntity> findServicesByCompanyId(UUID companyId, Pageable pageable);
    List<ServiceEntity> findServicesByCompanyId(UUID companyId);

    ServiceDurationRange findDurationRangeByCompanyId(UUID companyId);

    List<Integer> findAllMinuteDurationServicesByCompanyIdOrderAsc(UUID companyId);

    List<ServiceScheduleDTO> findServicesWithSchedulesByCompanyIdAndDays(UUID companyId, List<Integer> days);
}
