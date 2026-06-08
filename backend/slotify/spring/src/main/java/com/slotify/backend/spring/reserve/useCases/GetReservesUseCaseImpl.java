package com.slotify.backend.spring.reserve.useCases;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.reserve.components.fetch.FetchComponentsFactory;
import com.slotify.backend.spring.reserve.dtos.ReserveSummary;
import com.slotify.backend.spring.reserve.enums.ReserveFetchType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetReservesUseCaseImpl implements GetReservesUseCase {

    private final FetchComponentsFactory fetchComponentsFactory;
    @Override
    public Page<ReserveSummary> getReserves(UUID accountId, AccountType accountType, ReserveFetchType reserveFetchType, Pageable pageable) {

        return fetchComponentsFactory.forAccountType(accountType)
                .fetcher()
                .fetch(accountId, reserveFetchType, pageable)
                .map(ReserveSummary.class::cast);
    }
}
