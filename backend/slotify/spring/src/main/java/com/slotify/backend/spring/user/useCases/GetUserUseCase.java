package com.slotify.backend.spring.user.useCases;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.user.dtos.UserSummary;

import java.util.UUID;

public interface GetUserUseCase {
    UserSummary getUser(UUID userId, AccountType accountType, UUID accountId);
}
