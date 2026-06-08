package com.slotify.backend.spring.reserve.components.cancelation;

import com.slotify.backend.spring.reserve.models.ReserveEntity;

import java.util.UUID;

public interface ReserveCancelationValidator {

    void validate(ReserveEntity reserve, UUID accountId);
}
