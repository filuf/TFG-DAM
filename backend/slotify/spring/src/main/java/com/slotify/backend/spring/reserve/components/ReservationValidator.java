package com.slotify.backend.spring.reserve.components;

import com.slotify.backend.spring.exceptions.ReservationBadRequestException;
import com.slotify.backend.spring.exceptions.ReservationConflictException;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import com.slotify.backend.spring.reserve.services.ReserveService;
import com.slotify.backend.spring.service.enums.ScheduleLimits;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.models.ServiceScheduleEntity;
import com.slotify.backend.spring.service.services.ServiceScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationValidator {

    private final ServiceScheduleService serviceScheduleService;
    private final ReserveService reserveService;

    public void validateReservationWindow(LocalDateTime reserveDateTime) {
        LocalDateTime now = LocalDateTime.now();

        if (reserveDateTime.isBefore(now)) {
            throw new ReservationBadRequestException("No se puede realizar una reserva en una fecha o hora pasada");
        }

        if (reserveDateTime.isBefore(now.plusMinutes(30))) {
            throw new ReservationBadRequestException("Las reservas deben hacerse con al menos 30 minutos de antelación");
        }
    }

    public void validateServiceSchedule(ServiceEntity serviceEntity, LocalDateTime reserveDateTime) {
        DayOfWeek reserveDayOfWeek = reserveDateTime.getDayOfWeek();

        List<ServiceScheduleEntity> schedules = serviceScheduleService.findAllByServiceAndDayOfWeekIn(
                serviceEntity, List.of(reserveDayOfWeek.minus(1).getValue(), reserveDayOfWeek.getValue(), reserveDayOfWeek.plus(1).getValue())
        );
        schedules = schedules.stream()
                .map(scheduleEntity -> {
                    if (scheduleEntity.getStartTime().isAfter(scheduleEntity.getEndTime())) {
                        return ServiceScheduleEntity.builder()
                                .startTime(LocalTime.MIN)
                                .endTime(scheduleEntity.getEndTime())
                                .dayOfWeek(scheduleEntity.getDayOfWeek())
                                .service(scheduleEntity.getService())
                                .build();
                    }
                    return scheduleEntity;
        }).toList();

        LocalTime reserveTime = reserveDateTime.toLocalTime();
        boolean hourAvailable = schedules.stream()
                .anyMatch(schedule ->
                        (schedule.getStartTime().isBefore(reserveTime) || schedule.getStartTime().equals(reserveTime))
                                && (schedule.getEndTime().isAfter(reserveTime) || schedule.getEndTime().equals(reserveTime))
                );
        if (!hourAvailable) {
            throw new ReservationBadRequestException("El servicio no se oferta en ese horario");
        }
    }

    public void validateUserConfict(UUID userId, LocalDateTime reserveDateTime, LocalDateTime reserveEndTime) {
        List<ReserveEntity> userReservesInRange = this.reserveService.findAllReservesByUserIdBetweenDateTimes(
                userId,
                reserveDateTime.minusMinutes(ScheduleLimits.MAX_MINUTES_DURATION.getValue()),
                reserveEndTime.plusMinutes(ScheduleLimits.MIN_MINUTES_DURATION.getValue())
        );
        Optional<ReserveEntity> reserveOverlapOpt = userReservesInRange.stream()
                .filter(reserve -> {
                    LocalDateTime existingStart = reserve.getServiceTime();
                    LocalDateTime existingEnd = existingStart.plusMinutes(reserve.getService().getServiceMinutesDuration());

                    return existingStart.isBefore(reserveEndTime) && existingEnd.isAfter(reserveDateTime);
                }).findFirst();
        if (reserveOverlapOpt.isPresent()) {
            ReserveEntity overlap = reserveOverlapOpt.get();
            throw new ReservationConflictException("El usuario tiene una reserva en esa franja horaria", overlap.getReserveId());
        }
    }
}
