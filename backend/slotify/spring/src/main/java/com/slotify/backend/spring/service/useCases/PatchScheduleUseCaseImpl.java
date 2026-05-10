package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.exceptions.ScheduleValidationException;
import com.slotify.backend.spring.service.components.ScheduleDurationValidator;
import com.slotify.backend.spring.service.components.ScheduleOverlapValidator;
import com.slotify.backend.spring.service.dtos.ScheduleSummary;
import com.slotify.backend.spring.service.mappers.ServiceScheduleMapper;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.models.ServiceScheduleEntity;
import com.slotify.backend.spring.service.services.ServiceScheduleService;
import com.slotify.backend.spring.service.util.ScheduleFormatter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatchScheduleUseCaseImpl implements PatchScheduleUseCase {

    private final ServiceScheduleService serviceScheduleService;
    private final ScheduleDurationValidator scheduleDurationValidator;
    private final ScheduleOverlapValidator scheduleOverlapValidator;
    private final ServiceScheduleMapper serviceScheduleMapper;
    @Override
    @Transactional
    public ScheduleSummary patchSchedule(
            UUID scheduleId,
            UUID companyId,
            JsonNullable<Integer> dayOfWeek,
            JsonNullable<LocalTime> startTime,
            JsonNullable<LocalTime> endTime
    ) {
        log.info("El usuario: {}, intenta patchear un schedule: {}", companyId, scheduleId);

        ServiceScheduleEntity scheduleEntity = this.serviceScheduleService.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("No existe un horario en la base de datos con el id: " + scheduleId));


        ServiceEntity serviceEntity = scheduleEntity.getService();
        if (!serviceEntity.getCompany().getUserId().equals(companyId)) {
            log.warn("Un usuario de otra empresa intenta alternar un horario de forma ilegal, usuarioMalvado: {}, empresaAtacada: {}, scheduleAfectado: {}",
                    companyId, serviceEntity.getCompany().getCompanyName(), scheduleEntity.getId());
            throw new AccessDeniedException("No posees los permisos necesarios para modificar este recurso");
        }

        patchEntity(dayOfWeek, startTime, endTime, scheduleEntity);
        this.scheduleDurationValidator.validate(scheduleEntity.getStartTime(), scheduleEntity.getEndTime());

        validateSchedule(scheduleId, scheduleEntity, serviceEntity);

        return this.serviceScheduleMapper.toScheduleSummary(scheduleEntity);
    }

    private void validateSchedule(UUID scheduleId, ServiceScheduleEntity scheduleEntity, ServiceEntity serviceEntity) {
        DayOfWeek day = DayOfWeek.of(scheduleEntity.getDayOfWeek());
        List<Integer> days = List.of(
                day.minus(1).getValue(),
                day.getValue(),
                day.plus(1).getValue()
        );
        List<ServiceScheduleEntity> schedules = this.serviceScheduleService.findAllByServiceAndDayOfWeekIn(serviceEntity, days);
        schedules = schedules.stream().filter(schedule -> schedule.getId() != scheduleId).toList();

        List<ServiceScheduleEntity> overlaps = schedules.stream()
                .filter(schedule -> scheduleOverlapValidator.overlaps(
                        DayOfWeek.of(scheduleEntity.getDayOfWeek()), scheduleEntity.getStartTime(), scheduleEntity.getEndTime(),
                        DayOfWeek.of(schedule.getDayOfWeek()), schedule.getStartTime(), schedule.getEndTime())
                ).toList();
        if (!overlaps.isEmpty()) {
            List<String> overlapsFormatted = overlaps.stream().map(ScheduleFormatter::format).toList();
            throw new ScheduleValidationException("El rango de duración del servicio no puede colisionar con otros rangos", overlapsFormatted);
        }
    }

    private void patchEntity(JsonNullable<Integer> dayOfWeek, JsonNullable<LocalTime> startTime, JsonNullable<LocalTime> endTime, ServiceScheduleEntity scheduleEntity) {
        if (dayOfWeek.isPresent()) {
            scheduleEntity.setDayOfWeek(dayOfWeek.get());
        }

        if (startTime.isPresent()) {
            scheduleEntity.setStartTime(startTime.get());
        }

        if (endTime.isPresent()) {
            scheduleEntity.setEndTime(endTime.get());
        }
    }
}
