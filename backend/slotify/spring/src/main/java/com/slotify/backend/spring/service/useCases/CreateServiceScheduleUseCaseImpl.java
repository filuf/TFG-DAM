package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.exceptions.ScheduleValidationException;
import com.slotify.backend.spring.service.components.ScheduleDurationValidator;
import com.slotify.backend.spring.service.components.ScheduleOverlapValidator;
import com.slotify.backend.spring.service.dtos.CreateServiceScheduleResponse;
import com.slotify.backend.spring.service.mappers.ServiceScheduleMapper;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.models.ServiceScheduleEntity;
import com.slotify.backend.spring.service.services.ServiceScheduleService;
import com.slotify.backend.spring.service.services.ServiceService;
import com.slotify.backend.spring.service.util.ScheduleFormatter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class CreateServiceScheduleUseCaseImpl implements CreateServiceScheduleUseCase {

    private final ServiceService serviceService;
    private final ServiceScheduleService serviceScheduleService;
    private final ScheduleDurationValidator scheduleDurationValidator;
    private final ScheduleOverlapValidator scheduleOverlapValidator;
    private final ServiceScheduleMapper serviceScheduleMapper;

    @Override
    @Transactional
    public CreateServiceScheduleResponse createSchedule(UUID companyId, UUID serviceId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        log.info("El usuario: {}, intenta crear un schedule para el service: {}", companyId, serviceId);

        ServiceEntity serviceEntity = this.serviceService.findServiceById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("No existe un servicio en la base de datos con el id: " + serviceId));

        if (!serviceEntity.getCompany().getUserId().equals(companyId)) {
            log.warn("Un usuario de otra empresa intenta alternar un horario de forma ilegal, usuarioMalvado: {}, empresaAtacada: {}, servicioAtacado: {}",
                    companyId, serviceEntity.getCompany().getCompanyName(), serviceId);
            throw new AccessDeniedException("No posees los permisos necesarios para modificar este recurso");
        }

        this.scheduleDurationValidator.validate(startTime, endTime);

        List<Integer> days = List.of(
                dayOfWeek.minus(1).getValue(),
                dayOfWeek.getValue(),
                dayOfWeek.plus(1).getValue()
        );
        List<ServiceScheduleEntity> schedules = this.serviceScheduleService.findByServiceAndDayOfWeekIn(serviceEntity, days);

        List<ServiceScheduleEntity> overlaps = schedules.stream()
                .filter(schedule -> scheduleOverlapValidator.overlaps(
                        dayOfWeek, startTime, endTime,
                        DayOfWeek.of(schedule.getDayOfWeek()), schedule.getStartTime(), schedule.getEndTime())
                ).toList();
        if (!overlaps.isEmpty()) {
            List<String> overlapsFormatted = overlaps.stream().map(ScheduleFormatter::format).toList();
            throw new ScheduleValidationException("El rango de duración del servicio no puede colisionar con otros rangos", overlapsFormatted);
        }

        ServiceScheduleEntity scheduleEntity = this.serviceScheduleMapper.toEntity(serviceEntity, dayOfWeek, startTime, endTime);
        scheduleEntity = this.serviceScheduleService.saveSchedule(scheduleEntity);

        return this.serviceScheduleMapper.toCreateServiceScheduleResponse(scheduleEntity, dayOfWeek, serviceEntity);
    }
}
