package com.slotify.backend.spring.reserve.services;

import com.slotify.backend.spring.reserve.models.ReserveEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReserveService {

    ReserveEntity saveReserve(ReserveEntity reserve);
    List<ReserveEntity> findReservesByUserId(UUID userId);
    List<ReserveEntity> findAllReservesByUserIdBetweenDateTimes(UUID userId, LocalDateTime start, LocalDateTime end);
    List<ReserveEntity> findAllReservesByCompanyIdBetweenDateTimes(UUID companyId, LocalDateTime start, LocalDateTime end);
}
