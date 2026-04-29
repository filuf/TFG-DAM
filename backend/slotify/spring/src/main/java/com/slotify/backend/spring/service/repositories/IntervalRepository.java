package com.slotify.backend.spring.service.repositories;

import com.slotify.backend.spring.service.models.CompanyIntervalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IntervalRepository extends JpaRepository<CompanyIntervalEntity, UUID> {


}