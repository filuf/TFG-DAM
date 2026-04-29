package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.service.models.CompanyIntervalEntity;
import com.slotify.backend.spring.service.repositories.IntervalRepository;
import com.slotify.backend.spring.service.services.IntervalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntervalServiceImpl implements IntervalService {

    private final IntervalRepository intervalRepository;

    @Override
    public Optional<CompanyIntervalEntity> findById(UUID intervalId) {
        return intervalRepository.findById(intervalId);
    }
}