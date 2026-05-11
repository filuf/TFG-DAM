package com.slotify.backend.spring.reserve.useCases;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.reserve.components.fetch.FetchComponentsFactory;
import com.slotify.backend.spring.reserve.dtos.ReserveSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetReserveByIdUseCaseImpl implements GetReserveByIdUseCase {

    private final FetchComponentsFactory fetchComponentsFactory;
    @Override
    public ReserveSummary getReserve(UUID reserveId, UUID accountId, AccountType accountType) {
        return fetchComponentsFactory.forAccountType(accountType)
                .fetcher()
                .fetchById(reserveId, accountId, accountType);
    }
}
