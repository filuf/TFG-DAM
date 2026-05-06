package com.slotify.backend.spring.company.useCases;

import com.slotify.backend.spring.company.components.CompanyOwnershipValidator;
import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.models.CompanyIntervalEntity;
import com.slotify.backend.spring.company.services.IntervalService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteIntervalUseCaseImpl implements DeleteIntervalUseCase {

    private final IntervalService intervalService;
    private final CompanyOwnershipValidator companyOwnershipValidator;

    @Override
    public void deleteInterval(UUID companyId, UUID intervalId) {

        CompanyIntervalEntity intervalEntity = this.intervalService.findIntervalById(intervalId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe un intervalo en la base de datos con el id: " + intervalId
                ));

        CompanyEntity companyEntity = intervalEntity.getCompany();

        this.companyOwnershipValidator.verify(companyId, companyEntity);

        this.intervalService.deleteInterval(intervalEntity);
    }
}
