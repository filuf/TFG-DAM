package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.service.models.CompanyIntervalEntity;
import com.slotify.backend.spring.service.repositories.IntervalRepository;
import com.slotify.backend.spring.service.services.IntervalService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteIntervalUseCaseImpl implements DeleteIntervalUseCase {

    private final IntervalService intervalService;
    private final IntervalRepository intervalRepository;

    @Override
    public void deleteInterval(UUID companyId, UUID intervalId) {
        CompanyIntervalEntity interval = intervalService.findById(intervalId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe un intervalo con id: " + intervalId));

        CompanyEntity company = interval.getCompany();

        if (!company.getUserId().equals(companyId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No tienes permiso para eliminar este intervalo");
        }

        intervalRepository.delete(interval);
    }
}