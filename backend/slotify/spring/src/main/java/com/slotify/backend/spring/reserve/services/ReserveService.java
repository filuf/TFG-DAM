package com.slotify.backend.spring.reserve.services;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.reserve.enums.ReserveFetchType;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReserveService {

    ReserveEntity saveReserve(ReserveEntity reserve);
    Optional<ReserveEntity> findReserveById(UUID reserveId);
    List<ReserveEntity> findReservesByUserId(UUID userId);
    List<ReserveEntity> findAllReservesByUserIdBetweenDateTimes(UUID userId, LocalDateTime start, LocalDateTime end);
    List<ReserveEntity> findAllReservesByCompanyIdBetweenDateTimes(UUID companyId, LocalDateTime start, LocalDateTime end);


    Page<ReserveEntity> findAllReservesByAccountIdAndAccountTypeAndFetchType(
            UUID accountId,
            AccountType accountType,
            ReserveFetchType reserveFetchType,
            Pageable pageable
    );
}
