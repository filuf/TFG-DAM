package com.slotify.backend.spring.reserve.useCases;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.reserve.dtos.ReserveSummary;
import com.slotify.backend.spring.reserve.enums.ReserveFetchType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetReservesUseCase {

    Page<ReserveSummary> getReserves(UUID accountId, AccountType accountType, ReserveFetchType reserveFetchType, Pageable pageable);
}
