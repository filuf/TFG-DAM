package com.slotify.backend.spring.reserve.components.fetch.impl;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.reserve.components.fetch.ReserveFetcher;
import com.slotify.backend.spring.reserve.dtos.CompanyReserveSummary;
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
public class CompanyReserveFetcher implements ReserveFetcher<CompanyReserveSummary> {

    private final ReserveService reserveService;
    private final ReserveMapper reserveMapper;

    @Override
    public Page<CompanyReserveSummary> fetch(UUID accountId, ReserveFetchType reserveFetchType, Pageable pageable) {
        Page<ReserveEntity> reserves = this.reserveService.findAllReservesByAccountIdAndAccountTypeAndFetchType(
                accountId,
                AccountType.COMPANY,
                reserveFetchType,
                pageable
        );

        return reserves.map(this.reserveMapper::getCompanyReserveSummary);
    }



    @Override
    public CompanyReserveSummary fetchById(UUID reserveId, UUID accountId, AccountType accountType) {
        ReserveEntity reserve = this.reserveService.findReserveByIdAndAccountType(reserveId, accountId, accountType)
                .orElseThrow( () -> new EntityNotFoundException("No existe en la base de datos una reserva con el id: " + reserveId));

        if (!reserve.getService().getCompany().getUserId().equals(accountId)) {
            throw new AccessDeniedException("No posees los permisos necesarios para modificar ver este recurso");
        }

        return this.reserveMapper.getCompanyReserveSummary(reserve);
    }
}
