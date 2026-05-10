package com.slotify.backend.spring.reserve.useCases;

import com.slotify.backend.spring.auth.enums.AccountType;

import java.util.UUID;

public interface CancelReserveUseCase {

    void cancelReserve(UUID reserveId, UUID accountId, AccountType accountType);
}
