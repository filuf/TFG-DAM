package com.slotify.backend.spring.company.repositories;

import com.slotify.backend.spring.company.models.CompanyIntervalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IntervalRepository extends JpaRepository<CompanyIntervalEntity, UUID> {

    List<CompanyIntervalEntity> findByCompany_UserId(UUID userId);
}
