package com.slotify.backend.spring.reserve.components;

import com.slotify.backend.spring.exceptions.ReservationConflictException;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import com.slotify.backend.spring.service.projections.ServiceScheduleDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GapEfficiencyAnalyzer {

    /**
     * Valida si la reserva solicitada deja huecos inútiles en la agenda.
     * Permite cualquier horario siempre que quepa al menos otro servicio en el hueco restante,
     * o que el hueco sea el cierre natural de la jornada laboral.
     */
    public void validateGaps(LocalDateTime startReq, LocalDateTime endReq, List<ReserveEntity> reserves,
                             List<ServiceScheduleDTO> schedules, Integer workersAvailable) {
        log.info("startReq: {}, endReq: {}", startReq, endReq);

        // la reserva no deja un espacio inservible con la siguiente
        // el espacio libre permite que un servicio que se oferte en ese horario entre

        LocalDateTime serviceStart = findClosestServiceStart(startReq, schedules);
        LocalDateTime serviceEnd = findClosestServiceEnd(endReq, schedules);

        if (serviceStart == null || serviceEnd == null) {
            throw new ReservationConflictException("La empresa no oferta servicio en este horario");
        }

        LocalDateTime limitBefore = serviceStart;
        LocalDateTime limitAfter = serviceEnd;

        if (workersAvailable == 1) {
            LocalDateTime closestReserveEndBefore = findClosestReserveEndBefore(startReq, reserves);
            LocalDateTime closestReserveStartAfter = findClosestReserveStartAfter(endReq, reserves);

            limitBefore = closestReserveEndBefore != null ? closestReserveEndBefore : limitBefore;
            limitAfter = closestReserveStartAfter != null ? closestReserveStartAfter : limitAfter;
        }




        log.info("limitBefore: {}, limitAfter: {}", limitBefore, limitAfter);

        long startGap = ChronoUnit.MINUTES.between(limitBefore, startReq);
        long endGap = ChronoUnit.MINUTES.between(endReq, limitAfter);

        log.info("startGap: {}, endGap: {}", startGap, endGap);

        boolean isStartGapValid = false;

        if (startGap < 5) {
            isStartGapValid = true;
        } else {
            ServiceScheduleDTO smallestServiceBefore = findSmallestServiceBetween(limitBefore, startReq, schedules);
            if (smallestServiceBefore != null && (smallestServiceBefore.serviceMinutesDuration() <= startGap)) {
                isStartGapValid = true;
            }
        }

        log.info("isStarGapValid: {}", isStartGapValid);
        if (!isStartGapValid) {
            throw new ReservationConflictException("El horario de la reserva no es válido.");
        }

        boolean isEndGapValid = false;

        if (endGap < 5) {
            // Si el hueco es menor a 5 minutos, está pegado a la siguiente reserva o es el cierre de jornada
            isEndGapValid = true;
        } else {
            ServiceScheduleDTO smallestServiceAfter = findSmallestServiceBetween(endReq, limitAfter, schedules);
            if (smallestServiceAfter != null && smallestServiceAfter.serviceMinutesDuration() <= endGap) {
                isEndGapValid = true; // Es lo suficientemente grande para albergar otro servicio
            }
        }

        log.info("isEndGapValid: {}", isEndGapValid);
        if (!isEndGapValid) {
            throw new ReservationConflictException("El horario de la reserva no es válido.");
        }
    }

    public boolean areGapsValid(LocalDateTime startReq, LocalDateTime endReq, List<ReserveEntity> reserves,
                             List<ServiceScheduleDTO> schedules, Integer workersAvailable) {

        try {
            this.validateGaps(startReq, endReq, reserves, schedules, workersAvailable);
            return true;
        } catch (ReservationConflictException _e) {
            return false;
        }
    }

    private LocalDateTime findClosestReserveEndBefore(LocalDateTime startReq, List<ReserveEntity> reserves) {
        return reserves.stream()
                .map(r -> r.getServiceTime().plusMinutes(r.getService().getServiceMinutesDuration()))
                .filter(endReserve -> !endReserve.isAfter(startReq))
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    private LocalDateTime findClosestReserveStartAfter(LocalDateTime endReq, List<ReserveEntity> reserves) {
        return reserves.stream()
                .map(ReserveEntity::getServiceTime)
                .filter(startReserve -> !startReserve.isBefore(endReq))
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    /**
     * Localiza el inicio de jornada más cercano previo a la reserva.
     *
     * @param ref       Punto de referencia (inicio de la reserva).
     * @param schedules Lista de horarios configurados.
     * @return El LocalDateTime del inicio de servicio más cercano o null si no aplica.
     */
    private LocalDateTime findClosestServiceStart(LocalDateTime ref, List<ServiceScheduleDTO> schedules) {
        return schedules.stream()
                .map(s -> ref.toLocalDate().atTime(s.startTime()))
                .filter(start -> !start.isAfter(ref))
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    /**
     * Localiza el fin de jornada más cercano posterior a la reserva.
     *
     * Gestiona automáticamente el cruce de medianoche en horarios nocturnos sumando un día
     * al componente de fecha si el fin es cronológicamente anterior al inicio.
     *
     * @param ref       Punto de referencia (fin de la reserva).
     * @param schedules Lista de horarios configurados.
     * @return El LocalDateTime del fin de servicio más cercano o null si no aplica.
     */
    private LocalDateTime findClosestServiceEnd(LocalDateTime ref, List<ServiceScheduleDTO> schedules) {
        return schedules.stream()
                .map(s -> {
                    LocalDateTime end = ref.toLocalDate().atTime(s.endTime());
                    return end.isBefore(ref.toLocalDate().atTime(s.startTime())) ? end.plusDays(1) : end;
                })
                .filter(end -> !end.isBefore(ref))
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    private ServiceScheduleDTO findSmallestServiceBetween(LocalDateTime startRange, LocalDateTime endRange,
                                                          List<ServiceScheduleDTO> schedules) {
        ServiceScheduleDTO serviceScheduleDTO = schedules.stream()
                .filter(s -> {
                    LocalDate baseDate = startRange.toLocalDate();

                    LocalDate scheduleDate = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.of(s.dayOfWeek())));

                    if (ChronoUnit.DAYS.between(scheduleDate, baseDate) > 1) {
                        scheduleDate = baseDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.of(s.dayOfWeek())));
                    }

                    LocalDateTime serviceStart = scheduleDate.atTime(s.startTime());
                    LocalDateTime serviceEnd = scheduleDate.atTime(s.endTime());

                    // cruce medianoche
                    if (s.startTime().isAfter(s.endTime())) {
                        serviceEnd = serviceEnd.plusDays(1);
                    }

                    boolean overlaps = serviceStart.isBefore(endRange) && serviceEnd.isAfter(startRange);

                    if (!overlaps) {
                        return false;
                    }

                    // Calcula el espacio que queda libre ESTA jornada
                    LocalDateTime executionLimitStart = startRange.isBefore(serviceStart) ? serviceStart : startRange;
                    LocalDateTime executionLimitEnd = endRange.isAfter(serviceEnd) ? serviceEnd : endRange;

                    long availableMinutesInGap = ChronoUnit.MINUTES.between(executionLimitStart, executionLimitEnd);

                    // duración cabe en el hueco real de la jornada activa
                    return s.serviceMinutesDuration() <= availableMinutesInGap;
                })
                .min(Comparator.comparingInt(ServiceScheduleDTO::serviceMinutesDuration))
                .orElse(null);
        log.info("range: {} - {}, service: {}", startRange, endRange, serviceScheduleDTO);
        return serviceScheduleDTO; // Devuelve null si no hay ningún servicio disponible en ese rango
    }

}
