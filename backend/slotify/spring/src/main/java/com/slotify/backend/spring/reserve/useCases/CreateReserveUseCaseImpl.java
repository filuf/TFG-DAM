package com.slotify.backend.spring.reserve.useCases;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.reserve.components.*;
import com.slotify.backend.spring.reserve.dtos.CreateReserveResponse;
import com.slotify.backend.spring.reserve.dtos.ReservationCreatedEvent;
import com.slotify.backend.spring.reserve.mappers.ReserveMapper;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import com.slotify.backend.spring.service.projections.ServiceScheduleDTO;
import com.slotify.backend.spring.reserve.services.ReserveService;
import com.slotify.backend.spring.service.enums.ScheduleLimits;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.services.ServiceService;
import com.slotify.backend.spring.user.models.UserEntity;
import com.slotify.backend.spring.user.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateReserveUseCaseImpl implements CreateReserveUseCase {

    private final RedisLockReserveExecutor redisLockReserveExecutor;

    private final ReserveService reserveService;
    private final ServiceService serviceService;
    private final UserService userService;

    private final ReservationValidator reservationValidator;
    private final CapacityManager capacityManager;
    private final GapEfficiencyAnalyzer gapEfficiencyAnalyzer;
    private final ReserveMapper reserveMapper;

    private final ApplicationEventPublisher publisher;


    @Override
    @Transactional
    public CreateReserveResponse createReserve(UUID userId, UUID serviceId, LocalDateTime reserveDateTime) {

        UserEntity userEntity = this.userService.findUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("El usuario ha sido deshabilitado: " + userId));

        this.reservationValidator.validateReservationWindow(reserveDateTime);

        ServiceEntity serviceEntity = this.serviceService.findServiceByIdWithCompany(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("No existe un servicio en la base de datos con el id: " + serviceId));
        CompanyEntity companyEntity = serviceEntity.getCompany();

        this.reservationValidator.validateServiceSchedule(serviceEntity, reserveDateTime);

        LocalDateTime reserveEndTime = reserveDateTime.plusMinutes(serviceEntity.getServiceMinutesDuration());

        Integer maxConcurrentServices = this.capacityManager.calculateEffectiveCapacity(companyEntity, reserveDateTime, reserveEndTime);

        // el orden de los locks es importante para no bloquear a la empresa con una validación que se puede hacer solo con el usuario
        return this.redisLockReserveExecutor.userLock(userId.toString(), 10, () -> {
            this.reservationValidator.validateUserConfict(userId, reserveDateTime, reserveEndTime);

            return this.redisLockReserveExecutor.companyLock(companyEntity.getUserId().toString(), 10, () -> {
                List<ReserveEntity> companyReservesInRange = capacityManager.getCompanyReservesInDateRange(
                        companyEntity.getUserId(),
                        reserveDateTime.minusMinutes(ScheduleLimits.MAX_MINUTES_DURATION.getValue()),
                        reserveEndTime.plusMinutes(ScheduleLimits.MAX_MINUTES_DURATION.getValue())
                );

                this.capacityManager.validateInstantCapacity(companyEntity, companyReservesInRange, reserveDateTime, reserveEndTime, maxConcurrentServices);

                DayOfWeek reserveDayOfWeek = reserveDateTime.getDayOfWeek();
                List<ServiceScheduleDTO> companyServiceSchedules = this.serviceService.findServicesWithSchedulesByCompanyIdAndDays(companyEntity.getUserId(),
                        List.of(reserveDayOfWeek.minus(1).getValue(), reserveDayOfWeek.getValue(), reserveDayOfWeek.plus(1).getValue())
                );

                this.gapEfficiencyAnalyzer.validateGaps(reserveDateTime, reserveEndTime, companyReservesInRange, companyServiceSchedules);

                ReserveEntity reserveEntity = this.reserveService.saveReserve(
                        reserveMapper.toEntity(userEntity, serviceEntity, reserveDateTime, LocalDateTime.now(), false)
                );

                this.publisher.publishEvent(ReservationCreatedEvent.from(reserveEntity, userEntity, companyEntity, serviceEntity));

                return this.reserveMapper.toCreateReserveResponse(reserveDateTime, reserveEntity, serviceEntity, companyEntity);
            });

        });

    }
}
