package com.slotify.backend.spring.reserve.components.cancelation.impl;

import com.slotify.backend.spring.exceptions.ReservationBadRequestException;
import com.slotify.backend.spring.reserve.components.cancelation.ReserveCancelationValidator;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyCancelationReserveValidator implements ReserveCancelationValidator {

    private final CommonValidatorCancelation commonValidatorCancelation;
    private static final Integer MINIMUM_MINUTES_TO_CANCEL = 480;

    @Override
    public void validate(ReserveEntity reserve, UUID accountId) {

        UUID companyId = reserve.getService().getCompany().getUserId();

        if (!companyId.equals(accountId)) {
            throw new AccessDeniedException("No posees los permisos necesarios para modificar este recurso");
        }

        this.commonValidatorCancelation.validate(reserve, accountId);

        if (reserve.getServiceTime().minusMinutes(MINIMUM_MINUTES_TO_CANCEL).isBefore(LocalDateTime.now())) {
            throw new ReservationBadRequestException("No se puede cancelar la reserva con menos de " + MINIMUM_MINUTES_TO_CANCEL  / 60 + " horas de margen");
        }
    }
}
