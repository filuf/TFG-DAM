package com.slotify.backend.spring.service.services;

import com.slotify.backend.spring.service.models.CompanyIntervalEntity;

import java.util.Optional;
import java.util.UUID;

public interface IntervalService {
    Optional<CompanyIntervalEntity> findById(UUID intervalId);
}