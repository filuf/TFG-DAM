package com.slotify.backend.spring.reserve.services;

import com.slotify.backend.spring.reserve.models.ReserveEntity;
import com.slotify.backend.spring.reserve.repositories.ReserveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReserveServiceImpl implements ReserveService {

    private final ReserveRepository reserveRepository;

    @Override
    public ReserveEntity saveReserve(ReserveEntity reserve) {
        return this.reserveRepository.save(reserve);
    }

    @Override
    public List<ReserveEntity> findReservesByUserId(UUID userId) {
        return this.reserveRepository.findByUser_UserId(userId);
    }

    @Override
    public List<ReserveEntity> findAllReservesByUserIdBetweenDateTimes(UUID userId, LocalDateTime start, LocalDateTime end) {
        return this.reserveRepository.findByUser_UserIdAndServiceTimeBetweenAndIsCanceledFalse(userId, start, end);
    }

    @Override
    public List<ReserveEntity> findAllReservesByCompanyIdBetweenDateTimes(UUID companyId, LocalDateTime start, LocalDateTime end) {
        return this.reserveRepository.findByService_Company_UserIdAndServiceTimeBetweenAndIsCanceledFalse(companyId, start, end);
    }
}
