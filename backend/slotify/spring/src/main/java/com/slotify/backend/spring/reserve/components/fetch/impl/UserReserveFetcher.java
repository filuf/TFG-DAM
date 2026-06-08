package com.slotify.backend.spring.reserve.components.fetch.impl;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.reserve.components.fetch.ReserveFetcher;
import com.slotify.backend.spring.reserve.dtos.UserReserveSummary;
import com.slotify.backend.spring.reserve.enums.ReserveFetchType;
import com.slotify.backend.spring.reserve.mappers.ReserveMapper;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import com.slotify.backend.spring.reserve.services.ReserveService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserReserveFetcher implements ReserveFetcher<UserReserveSummary> {

    private final ReserveService reserveService;
    private final ReserveMapper reserveMapper;

    @Override
    public Page<UserReserveSummary> fetch(UUID accountId, ReserveFetchType reserveFetchType, Pageable pageable) {
        Page<ReserveEntity> reserves = this.reserveService.findAllReservesByAccountIdAndAccountTypeAndFetchType(
                accountId,
                AccountType.USER,
                reserveFetchType,
                pageable
        );

        return reserves.map(this.reserveMapper::getUserReserveSummary);
    }



    @Override
    public UserReserveSummary fetchById(UUID reserveId, UUID accountId, AccountType accountType) {
        ReserveEntity reserve = this.reserveService.findReserveByIdAndAccountType(reserveId, accountId, accountType)
                .orElseThrow( () -> new EntityNotFoundException("No existe en la base de datos una reserva con el id: " + reserveId));

        if (!reserve.getUser().getUserId().equals(accountId)) {
            throw new AccessDeniedException("No posees los permisos necesarios para ver este recurso");
        }

        return this.reserveMapper.getUserReserveSummary(reserve);
    }
}
