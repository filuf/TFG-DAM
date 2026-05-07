package com.slotify.backend.spring.reserve.components.cancelation.impl;

import com.slotify.backend.spring.exceptions.ReservationBadRequestException;
import com.slotify.backend.spring.reserve.components.cancelation.ReserveCancelationValidator;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CommonValidatorCancelation implements ReserveCancelationValidator {

    @Override
    public void validate(ReserveEntity reserve, UUID accountId) {

        if (reserve.getServiceTime().isBefore(LocalDateTime.now())) {
            throw new ReservationBadRequestException("No se puede cancelar una reserva pasada");
        }

        if (reserve.isCanceled()) {
            throw new ReservationBadRequestException("No se puede cancelar una reserva que ya está cancelada");
        }

    }
}
