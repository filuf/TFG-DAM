package com.slotify.backend.spring.service.repositories;

import com.slotify.backend.spring.service.models.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceRepository extends JpaRepository<ServiceEntity, UUID> {

    Page<ServiceEntity> findByCompany_UserId(UUID userId, Pageable pageable);

}
