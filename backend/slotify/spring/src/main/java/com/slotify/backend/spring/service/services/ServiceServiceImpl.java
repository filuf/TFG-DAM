package com.slotify.backend.spring.service.services;

import com.slotify.backend.spring.service.projections.ServiceScheduleDTO;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.projections.ServiceDurationRange;
import com.slotify.backend.spring.service.repositories.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public Optional<ServiceEntity> findServiceByIdWithCompany(UUID serviceId) {
        return this.serviceRepository.findWithCompanyByServiceId(serviceId);
    }

    @Override
    public Page<ServiceEntity> findServicesByCompanyId(UUID companyId, Pageable pageable) {
        return this.serviceRepository.findByCompany_UserId(companyId, pageable);
    }

    @Override
    public List<ServiceEntity> findServicesByCompanyId(UUID companyId) {
        return this.serviceRepository.findByCompany_UserId(companyId);
    }

    @Override
    public ServiceDurationRange findDurationRangeByCompanyId(UUID companyId) {
        return this.serviceRepository.findDurationRangeByCompanyId(companyId);
    }

    @Override
    public List<Integer> findAllMinuteDurationServicesByCompanyIdOrderAsc(UUID companyId) {
        return this.serviceRepository.findDistinctDurationsByCompanyIdOrderAsc(companyId);
    }

    @Override
    public List<ServiceScheduleDTO> findServicesWithSchedulesByCompanyIdAndDays(UUID companyId, List<Integer> days) {
        return this.serviceRepository.findServiceSchedulesDTOByCompanyIdAndDays(companyId, days);
    }
}
