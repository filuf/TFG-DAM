package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.models.CompanyIntervalEntity;
import com.slotify.backend.spring.company.services.IntervalService;
import com.slotify.backend.spring.exceptions.ReservationBadRequestException;
import com.slotify.backend.spring.reserve.components.CapacityManager;
import com.slotify.backend.spring.reserve.components.GapEfficiencyAnalyzer;
import com.slotify.backend.spring.reserve.components.TimeCompactor;
import com.slotify.backend.spring.reserve.dtos.TimeIntervalDTO;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.models.ServiceScheduleEntity;
import com.slotify.backend.spring.service.projections.ServiceScheduleDTO;
import com.slotify.backend.spring.service.services.ServiceScheduleService;
import com.slotify.backend.spring.service.services.ServiceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetServiceSlotsAvailableUseCaseImpl implements GetServiceSlotsAvailableUseCase {

    private final ServiceService serviceService;
    private final ServiceScheduleService serviceScheduleService;
    private final IntervalService intervalService;

    private final CapacityManager capacityManager;
    private final GapEfficiencyAnalyzer gapEfficiencyAnalyzer;
    private final TimeCompactor timeCompactor;
    @Override
    @Transactional
    public List<TimeIntervalDTO> getServiceSlotsAvailable(UUID serviceId, LocalDate date) {

        if (date.isBefore(LocalDate.now())) {
            throw new ReservationBadRequestException("No se pueden listar horarios de fechas pasadas");
        }

        ServiceEntity serviceEntity = this.serviceService.findServiceByIdWithCompany(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("No existe un servicio en la base de datos con el id: " + serviceId));
        CompanyEntity companyEntity = serviceEntity.getCompany();

        List<CompanyIntervalEntity> intervalsThisDay = this.intervalService.findIntervalByCompanyIdAndBetweenDatesTime(
                companyEntity.getUserId(), LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));

        List<ReserveEntity> companyReservesInRange = capacityManager.getCompanyReservesInDateRange(
                companyEntity.getUserId(),
                LocalDateTime.of(date, LocalTime.MIN),
                LocalDateTime.of(date, LocalTime.MAX)
        )
                .stream()
                .filter(com -> !com.isCanceled())
                .toList();

        DayOfWeek reserveDayOfWeek = date.getDayOfWeek();
        List<ServiceScheduleEntity> schedules = this.serviceScheduleService.findAllByServiceAndDayOfWeekIn(
                serviceEntity, List.of(reserveDayOfWeek.minus(1).getValue(),reserveDayOfWeek.getValue()));

        List<ServiceScheduleEntity> schedulesNormalized = schedules.stream()
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
                })
                .toList();
        log.info("schedulesNormalized: {}", schedulesNormalized);

        List<ServiceScheduleDTO> companyServiceSchedules = this.serviceService.findServicesWithSchedulesByCompanyIdAndDays(companyEntity.getUserId(),
                List.of(reserveDayOfWeek.minus(1).getValue(), reserveDayOfWeek.getValue(), reserveDayOfWeek.plus(1).getValue())
        );
        log.info("companyServiceSchedules: {}", companyServiceSchedules);

        int totalMinutesInDay = (int) (ChronoUnit.MINUTES.between(LocalTime.MIN, LocalTime.MAX) + 1);
        List<LocalTime> timesAvailable = IntStream.range(LocalTime.MIN.getMinute(), totalMinutesInDay)
                .mapToObj(LocalTime.MIN::plusMinutes)
                .filter(reserveStartTime -> {

                    LocalTime reserveEndTime = reserveStartTime.plusMinutes(serviceEntity.getServiceMinutesDuration());

                    boolean isMinuteAvailable = this.isMinuteAvailable(serviceId, date, schedules, reserveStartTime, reserveEndTime);
                    if (!isMinuteAvailable) {
                        return false;
                    }

                    LocalDateTime startReserveDateTime = LocalDateTime.of(date, reserveStartTime);
                    LocalDateTime endReserveDateTime = LocalDateTime.of(date, reserveEndTime);

                    Integer maxConcurrentServices = getMaxConcurrentServices(intervalsThisDay, startReserveDateTime, endReserveDateTime, companyEntity);

                    Integer workersAvailable = this.capacityManager.getInstantCapacity(companyReservesInRange, startReserveDateTime, endReserveDateTime, maxConcurrentServices);
                    if (workersAvailable < 1) {
                        return false;
                    }

                    return this.gapEfficiencyAnalyzer.areGapsValid(
                            startReserveDateTime, endReserveDateTime, companyReservesInRange, companyServiceSchedules, workersAvailable
                    );
                })
                .toList();

        return this.timeCompactor.compactConsecutiveMinutes(timesAvailable);
    }

    private Integer getMaxConcurrentServices(List<CompanyIntervalEntity> intervalsThisDay, LocalDateTime startReserveDateTime, LocalDateTime endReserveDateTime, CompanyEntity companyEntity) {
        return intervalsThisDay.stream()
                .filter(interval ->
                        startReserveDateTime.isBefore(interval.getEndDatetime())
                                && interval.getStartDatetime().isBefore(endReserveDateTime)
                )
                .findFirst()
                .map(CompanyIntervalEntity::getMaxConcurrentServices)
                .orElse(companyEntity.getDefaultMaxConcurrentServices());
    }

    private boolean isMinuteAvailable(UUID serviceId, LocalDate date, List<ServiceScheduleEntity> schedules, LocalTime reserveStartTime, LocalTime reserveEndTime) {
        LocalDateTime requestStart = LocalDateTime.of(date, reserveStartTime);

        // cruce medianoche en reserva
        LocalDateTime requestEnd = reserveEndTime.isBefore(reserveStartTime)
                ? LocalDateTime.of(date.plusDays(1), reserveEndTime)
                : LocalDateTime.of(date, reserveEndTime);

        boolean minuteAvailable =  schedules.stream()
                .filter(schedule -> schedule.getService().getServiceId().equals(serviceId))
                .anyMatch(schedule -> {
                    DayOfWeek scheduleDayOfWeek = DayOfWeek.of(schedule.getDayOfWeek());

                    LocalDate scheduleDate = date.with(TemporalAdjusters.previousOrSame(scheduleDayOfWeek));

                    // más de un día de diferencia = hoy o mañana
                    if (ChronoUnit.DAYS.between(scheduleDate, date) > 1) {
                        scheduleDate = date.with(TemporalAdjusters.nextOrSame(scheduleDayOfWeek));
                    }

                    LocalDateTime scheduleStart = scheduleDate.atTime(schedule.getStartTime());
                    LocalDateTime scheduleEnd = scheduleDate.atTime(schedule.getEndTime());

                    // cruce medianoche en schedule
                    if (schedule.getStartTime().isAfter(schedule.getEndTime())) {
                        scheduleEnd = scheduleEnd.plusDays(1);
                    }

                    return !requestStart.isBefore(scheduleStart) && !requestEnd.isAfter(scheduleEnd);
                });
        log.info("reserveStartTime: {}, available: {}", reserveStartTime, minuteAvailable);

        return minuteAvailable;
    }
}
