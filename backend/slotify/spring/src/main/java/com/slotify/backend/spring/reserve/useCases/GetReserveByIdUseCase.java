package com.slotify.backend.spring.reserve.useCases;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.reserve.dtos.ReserveSummary;

import java.util.UUID;

public interface GetReserveByIdUseCase {
    ReserveSummary getReserve(UUID reserveId, UUID uuid, AccountType accountType);
}
