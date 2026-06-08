package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.exceptions.ScheduleValidationException;
import com.slotify.backend.spring.service.components.ScheduleDurationValidator;
import com.slotify.backend.spring.service.components.ScheduleOverlapValidator;
import com.slotify.backend.spring.service.dtos.CreateServiceScheduleResponse;
import com.slotify.backend.spring.service.mappers.ServiceScheduleMapper;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.models.ServiceScheduleEntity;
import com.slotify.backend.spring.service.services.ServiceScheduleService;
import com.slotify.backend.spring.service.services.ServiceService;
import jakarta.persistence.EntityNotFoundException;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateServiceScheduleUseCaseImplTest {

    private CreateServiceScheduleUseCaseImpl createServiceScheduleUseCase;
    @Mock
    private ServiceService serviceService;
    @Mock
    private ServiceScheduleService serviceScheduleService;
    @Mock
    private ScheduleDurationValidator scheduleDurationValidator;
    @Mock
    private ScheduleOverlapValidator scheduleOverlapValidator;
    @Mock
    private ServiceScheduleMapper serviceScheduleMapper;


    @BeforeEach
    void setUp() {
        this.createServiceScheduleUseCase = new CreateServiceScheduleUseCaseImpl(
                serviceService,
                serviceScheduleService,
                scheduleDurationValidator,
                scheduleOverlapValidator,
                serviceScheduleMapper
        );
    }

    @Test
    void createSchedule_whenUserNotExist_shouldThrowEntityNotFound() {

        when(serviceService.findServiceById(any()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> createServiceScheduleUseCase.createSchedule(UUID.randomUUID(), UUID.randomUUID(), DayOfWeek.MONDAY, LocalTime.MIN, LocalTime.MIN));
    }

    @Test
    void createSchedule_whenUserNotOwner_shouldThrowAccessDeniedException() {
        // Arrange
        UUID companyId = UUID.randomUUID();
        UUID otherCompanyId = UUID.randomUUID();

        // Usamos Instancio para setear el valor directamente en el campo aunque no haya setter
        ServiceEntity serviceEntity = Instancio.of(ServiceEntity.class)
                .set(field(CompanyEntity::getUserId), otherCompanyId)
                .create();

        when(serviceService.findServiceById(any())).thenReturn(Optional.of(serviceEntity));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
                createServiceScheduleUseCase.createSchedule(companyId, UUID.randomUUID(), DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(10, 0))
        );
        verify(scheduleDurationValidator, never()).validate(any(), any());
    }

    @Test
    void createSchedule_whenDurationInvalid_shouldThrowException() {
        // Arrange
        UUID companyId = UUID.randomUUID();
        ServiceEntity serviceEntity = Instancio.of(ServiceEntity.class)
                .set(field(CompanyEntity::getUserId), companyId)
                .create();

        when(serviceService.findServiceById(any())).thenReturn(Optional.of(serviceEntity));
        doThrow(new IllegalArgumentException("Invalid duration"))
                .when(scheduleDurationValidator).validate(any(), any());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                createServiceScheduleUseCase.createSchedule(companyId, UUID.randomUUID(), DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(9, 0))
        );
    }

    @Test
    void createSchedule_whenScheduleOverlaps_shouldThrowScheduleValidationException() {
        // Arrange
        UUID companyId = UUID.randomUUID();
        ServiceEntity serviceEntity = Instancio.of(ServiceEntity.class)
                .set(field(CompanyEntity::getUserId), companyId)
                .create();

        ServiceScheduleEntity existingSchedule = Instancio.of(ServiceScheduleEntity.class)
                .set(field(ServiceScheduleEntity::getDayOfWeek), DayOfWeek.MONDAY.getValue())
                .set(field(ServiceScheduleEntity::getStartTime), LocalTime.of(9, 0))
                .set(field(ServiceScheduleEntity::getEndTime), LocalTime.of(11, 0))
                .create();

        when(serviceService.findServiceById(any())).thenReturn(Optional.of(serviceEntity));
        when(serviceScheduleService.findAllByServiceAndDayOfWeekIn(eq(serviceEntity), anyList()))
                .thenReturn(List.of(existingSchedule));

        when(scheduleOverlapValidator.overlaps(any(), any(), any(), any(), any(), any()))
                .thenReturn(true);

        // Act & Assert
        assertThrows(ScheduleValidationException.class, () ->
                createServiceScheduleUseCase.createSchedule(companyId, UUID.randomUUID(), DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0))
        );
    }

    @Test
    void createSchedule_whenDataIsValid_shouldReturnResponse() {
        // Arrange
        UUID companyId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        DayOfWeek day = DayOfWeek.MONDAY;
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(10, 0);

        ServiceEntity serviceEntity = Instancio.of(ServiceEntity.class)
                .set(field(CompanyEntity::getUserId), companyId)
                .create();

        ServiceScheduleEntity scheduleEntity = Instancio.create(ServiceScheduleEntity.class);
        CreateServiceScheduleResponse expectedResponse = Instancio.create(CreateServiceScheduleResponse.class);

        when(serviceService.findServiceById(serviceId)).thenReturn(Optional.of(serviceEntity));
        when(serviceScheduleService.findAllByServiceAndDayOfWeekIn(eq(serviceEntity), anyList()))
                .thenReturn(List.of());

        when(serviceScheduleMapper.toEntity(serviceEntity, day, start, end)).thenReturn(scheduleEntity);
        when(serviceScheduleService.saveSchedule(scheduleEntity)).thenReturn(scheduleEntity);
        when(serviceScheduleMapper.toCreateServiceScheduleResponse(scheduleEntity, day, serviceEntity))
                .thenReturn(expectedResponse);

        // Act
        CreateServiceScheduleResponse actualResponse = createServiceScheduleUseCase.createSchedule(companyId, serviceId, day, start, end);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }
}