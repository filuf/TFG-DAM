package com.slotify.backend.spring.reserve.components;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.models.CompanyIntervalEntity;
import com.slotify.backend.spring.company.services.IntervalService;
import com.slotify.backend.spring.exceptions.ReservationConflictException;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import com.slotify.backend.spring.reserve.services.ReserveService;
import com.slotify.backend.spring.service.enums.ScheduleLimits;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CapacityManager {

    private final IntervalService intervalService;
    private final ReserveService reserveService;

    public Integer calculateEffectiveCapacity(CompanyEntity company, LocalDateTime reserveDateTime, LocalDateTime reserveEndTime) {
        return this.intervalService.findIntervalByCompanyIdAndBetweenDatesTime(company.getUserId(), reserveDateTime, reserveEndTime)
                .stream()
                .mapToInt(CompanyIntervalEntity::getMaxConcurrentServices)
                .min()
                .orElse(company.getDefaultMaxConcurrentServices());
    }

    public List<ReserveEntity> getCompanyReservesInDateRange(UUID userId, LocalDateTime reserveDateTime, LocalDateTime reserveEndTime) {
        return this.reserveService.findAllReservesByCompanyIdBetweenDateTimes(
                userId,
                reserveDateTime.minusMinutes(ScheduleLimits.MAX_MINUTES_DURATION.getValue()),
                reserveEndTime.plusMinutes(ScheduleLimits.MAX_MINUTES_DURATION.getValue())
        );
    }

    public void validateInstantCapacity(CompanyEntity company, List<ReserveEntity> companyReservesInRange, LocalDateTime reserveDateTime, LocalDateTime reserveEndTime, Integer maxConcurrentServices) {

        Long companyReservesInDateTime = companyReservesInRange.stream()
                .filter(reserve -> {
                    LocalDateTime existingStart = reserve.getServiceTime();
                    LocalDateTime existingEnd = existingStart.plusMinutes(reserve.getService().getServiceMinutesDuration());

                    return existingStart.isBefore(reserveEndTime) && existingEnd.isAfter(reserveDateTime);
                }).count();

        if (maxConcurrentServices <= companyReservesInDateTime) {
            throw new ReservationConflictException("La empresa " + company.getCompanyName() + " no tiene cupos libres en esta franja horaria");
        }
    }
}
