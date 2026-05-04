package com.slotify.backend.spring.reserve.components;

import com.slotify.backend.spring.exceptions.ReservationConflictException;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import com.slotify.backend.spring.service.projections.ServiceScheduleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GapEfficiencyAnalyzer {

    /**
     * Valida si la reserva solicitada deja huecos inútiles en la agenda.
     *
     * Analiza los "vecinos" inmediatos de la reserva (la reserva anterior y la posterior,
     * o en su defecto, el inicio y fin de la jornada laboral) y verifica si el espacio
     * resultante permite albergar al menos un servicio de la empresa.
     *
     * @param startReq  Fecha y hora de inicio de la reserva solicitada.
     * @param endReq    Fecha y hora de finalización de la reserva solicitada.
     * @param reserves  Lista de reservas existentes en el rango de influencia.
     * @param schedules Proyecciones de los horarios y duraciones de los servicios de la empresa.
     * @throws ReservationConflictException Si el hueco dejado es menor a la duración del servicio más corto.
     */
    public void validateGaps(LocalDateTime startReq, LocalDateTime endReq, List<ReserveEntity> reserves,
                             List<ServiceScheduleDTO> schedules) {

        LocalDateTime nearestBefore = reserves.stream()
                .map(r -> r.getServiceTime().plusMinutes(r.getService().getServiceMinutesDuration()))
                .filter(end -> !end.isAfter(startReq))
                .max(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime nearestAfter = reserves.stream()
                .map(ReserveEntity::getServiceTime)
                .filter(start -> !start.isBefore(endReq))
                .min(LocalDateTime::compareTo)
                .orElse(null);

        if (nearestBefore != null) {
            // Caso A: Hay una reserva antes. Validamos el hueco entre ambas.
            checkUtility(nearestBefore, startReq, schedules);
        } else {
            // Caso B: Es la "primera" reserva.
            // Validamos si el hueco que deja contra el inicio de los horarios es útil.
            // Buscamos el inicio de servicio más cercano al inicio de nuestra reserva.
            LocalDateTime serviceStartLimit = findClosestServiceStart(startReq, schedules);
            if (serviceStartLimit != null && serviceStartLimit.isBefore(startReq)) {
                checkUtility(serviceStartLimit, startReq, schedules);
            }
        }

        if (nearestAfter != null) {
            checkUtility(endReq, nearestAfter, schedules);
        } else {
            // Caso C: Es la "última" reserva.
            // Validamos contra el final de la disponibilidad de los servicios.
            LocalDateTime serviceEndLimit = findClosestServiceEnd(endReq, schedules);
            if (serviceEndLimit != null && serviceEndLimit.isAfter(endReq)) {
                checkUtility(endReq, serviceEndLimit, schedules);
            }
        }
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

    /**
     * Verifica la viabilidad comercial de un hueco de tiempo (Gap).
     *
     * Un hueco se considera "útil" si su duración en minutos es mayor o igual a la
     * duración mínima de cualquiera de los servicios que la empresa ofrece en ese
     * horario específico. Si el hueco es de 0 minutos (reservas contiguas), se considera válido.
     *
     * @param gapStart  Inicio del espacio vacío.
     * @param gapEnd    Fin del espacio vacío.
     * @param schedules Servicios y horarios de la empresa para comparar duraciones.
     * @throws ReservationConflictException Si el hueco existe pero es demasiado pequeño para ser usado.
     */
    private void checkUtility(LocalDateTime gapStart, LocalDateTime gapEnd, List<ServiceScheduleDTO> schedules) {
        long gapMinutes = Duration.between(gapStart, gapEnd).toMinutes();
        if (gapMinutes <= 0) return;

        boolean gapIsUseful = schedules.stream().anyMatch(s -> {
            if (s.serviceMinutesDuration() > gapMinutes) return false;

            LocalDateTime serviceStart = gapStart.toLocalDate().atTime(s.startTime());
            LocalDateTime serviceEnd = gapStart.toLocalDate().atTime(s.endTime());

            // Manejo cruce de medianoche en el horario del servicio
            if (serviceEnd.isBefore(serviceStart)) {
                serviceEnd = serviceEnd.plusDays(1);
            }

            // El servicio es útil si su ventana de tiempo permite realizarlo dentro del gap
            // O al menos si el gap está contenido en el horario del servicio
            return !serviceStart.isAfter(gapEnd) && !serviceEnd.isBefore(gapStart);
        });

        if (!gapIsUseful) {
            throw new ReservationConflictException("La hora seleccionada no está disponible para este servicio");
        }
    }
}
