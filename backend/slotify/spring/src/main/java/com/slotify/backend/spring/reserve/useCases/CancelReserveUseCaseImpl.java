package com.slotify.backend.spring.reserve.useCases;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.reserve.components.cancelation.CancelationComponentsFactory;
import com.slotify.backend.spring.reserve.components.cancelation.CancelationComponentsIf;
import com.slotify.backend.spring.reserve.dtos.ReservationCanceledEvent;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import com.slotify.backend.spring.reserve.services.ReserveService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CancelReserveUseCaseImpl implements CancelReserveUseCase {

    private final CancelationComponentsFactory cancelationComponentsFactory;
    private final ReserveService reserveService;
    @Override
    @Transactional
    public void cancelReserve(UUID reserveId, UUID accountId, AccountType accountType) {

        ReserveEntity reserveEntity = this.reserveService.findReserveById(reserveId)
                .orElseThrow(() -> new EntityNotFoundException("No existe una reserva con el ID: " + reserveId));

        CancelationComponentsIf cancelationComponents = cancelationComponentsFactory.forAccountType(accountType);

        cancelationComponents.validator()
                .validate(reserveEntity, accountId);

        cancelationComponents.cancelator()
                .cancel(reserveEntity);

        cancelationComponents.eventPublisher()
                .publish(new ReservationCanceledEvent(reserveEntity, accountId, accountType));
    }
}
