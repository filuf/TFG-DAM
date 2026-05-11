package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.exceptions.ScheduleValidationException;
import com.slotify.backend.spring.service.components.ScheduleDurationValidator;
import com.slotify.backend.spring.service.components.ScheduleOverlapValidator;
import com.slotify.backend.spring.service.dtos.ScheduleSummary;
import com.slotify.backend.spring.service.mappers.ServiceScheduleMapper;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.models.ServiceScheduleEntity;
import com.slotify.backend.spring.service.services.ServiceScheduleService;
import jakarta.persistence.EntityNotFoundException;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.security.access.AccessDeniedException;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatchScheduleUseCaseImplTest {

    private PatchScheduleUseCaseImpl patchScheduleUseCase;
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
        this.patchScheduleUseCase = new PatchScheduleUseCaseImpl(
                serviceScheduleService,
                scheduleDurationValidator,
                scheduleOverlapValidator,
                serviceScheduleMapper
        );
    }

    @Test
    void patchSchedule_whenScheduleDoesNotExist_shouldThrowEntityNotFoundException() {
        // Arrange
        UUID scheduleId = UUID.randomUUID();
        when(serviceScheduleService.findById(scheduleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                patchScheduleUseCase.patchSchedule(scheduleId, UUID.randomUUID(), JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined())
        );
    }

    @Test
    void patchSchedule_whenUserNotOwner_shouldThrowAccessDeniedException() {
        // Arrange
        UUID companyId = UUID.randomUUID();
        UUID otherCompanyId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();

        // Creamos la entidad con un dueño distinto
        ServiceScheduleEntity scheduleEntity = Instancio.of(ServiceScheduleEntity.class)
                .set(field("id"), scheduleId)
                .set(field(CompanyEntity::getUserId), otherCompanyId)
                .create();

        when(serviceScheduleService.findById(scheduleId)).thenReturn(Optional.of(scheduleEntity));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
                patchScheduleUseCase.patchSchedule(scheduleId, companyId, JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined())
        );
    }

    @Test
    void patchSchedule_whenOverlaps_shouldThrowScheduleValidationException() {
        // Arrange
        UUID companyId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        UUID otherScheduleId = UUID.randomUUID(); // ID diferente garantizado

        // Construimos la empresa primero para asegurar el link
        CompanyEntity company = Instancio.of(CompanyEntity.class)
                .set(field(CompanyEntity::getUserId), companyId)
                .create();

        ServiceEntity service = Instancio.of(ServiceEntity.class)
                .set(field(ServiceEntity::getCompany), company)
                .create();

        // Entidad a patchear
        ServiceScheduleEntity scheduleToPatch = Instancio.of(ServiceScheduleEntity.class)
                .set(field(ServiceScheduleEntity::getId), scheduleId)
                .set(field(ServiceScheduleEntity::getService), service)
                .set(field(ServiceScheduleEntity::getDayOfWeek), DayOfWeek.MONDAY.getValue())
                .create();

        // Otra entidad que provocará el solape (con ID distinto para que no la filtre el stream)
        ServiceScheduleEntity otherSchedule = Instancio.of(ServiceScheduleEntity.class)
                .set(field(ServiceScheduleEntity::getId), otherScheduleId)
                .set(field(ServiceScheduleEntity::getDayOfWeek), DayOfWeek.MONDAY.getValue())
                .create();

        when(serviceScheduleService.findById(scheduleId)).thenReturn(Optional.of(scheduleToPatch));

        // El servicio devuelve ambos
        when(serviceScheduleService.findAllByServiceAndDayOfWeekIn(eq(service), anyList()))
                .thenReturn(List.of(scheduleToPatch, otherSchedule));

        // Forzamos el true en el validador
        when(scheduleOverlapValidator.overlaps(
                any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class),
                any(DayOfWeek.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(true);

        // Act & Assert
        JsonNullable<LocalTime> newStart = JsonNullable.of(LocalTime.of(10, 0));

        assertThrows(ScheduleValidationException.class, () ->
                patchScheduleUseCase.patchSchedule(
                        scheduleId,
                        companyId,
                        JsonNullable.undefined(),
                        newStart,
                        JsonNullable.undefined()
                )
        );
    }

    @Test
    void patchSchedule_whenDataIsValid_shouldReturnSummary() {
        // Arrange
        UUID companyId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        LocalTime newStartTime = LocalTime.of(14, 0);

        ServiceScheduleEntity scheduleEntity = Instancio.of(ServiceScheduleEntity.class)
                .set(field("id"), scheduleId)
                .set(field(CompanyEntity::getUserId), companyId)
                .set(field(ServiceScheduleEntity::getDayOfWeek), 1) // Lunes
                .create();

        ScheduleSummary expectedSummary = Instancio.create(ScheduleSummary.class);

        when(serviceScheduleService.findById(scheduleId)).thenReturn(Optional.of(scheduleEntity));
        when(serviceScheduleService.findAllByServiceAndDayOfWeekIn(any(), anyList()))
                .thenReturn(List.of(scheduleEntity)); // Solo se encuentra a sí mismo, no hay solape

        when(serviceScheduleMapper.toScheduleSummary(scheduleEntity)).thenReturn(expectedSummary);

        // Act
        ScheduleSummary result = patchScheduleUseCase.patchSchedule(
                scheduleId,
                companyId,
                JsonNullable.undefined(),
                JsonNullable.of(newStartTime),
                JsonNullable.undefined()
        );

        // Assert
        assertNotNull(result);
        assertEquals(expectedSummary, result);
        assertEquals(newStartTime, scheduleEntity.getStartTime()); // Verificamos que el patch ocurrió

        verify(scheduleDurationValidator).validate(eq(newStartTime), any());
        verify(serviceScheduleMapper).toScheduleSummary(scheduleEntity);
    }
    @Test
    void patchSchedule_shouldUpdateDayOfWeekAndEndTime_whenPresentInJsonNullable() {
        // Arrange
        UUID companyId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        Integer newDay = DayOfWeek.FRIDAY.getValue();
        LocalTime newEndTime = LocalTime.of(18, 0);

        // Configuramos la entidad con Instancio
        ServiceScheduleEntity scheduleEntity = Instancio.of(ServiceScheduleEntity.class)
                .set(field(ServiceScheduleEntity::getId), scheduleId)
                .set(field(ServiceScheduleEntity::getDayOfWeek), DayOfWeek.MONDAY.getValue())
                .set(field(ServiceScheduleEntity::getStartTime), LocalTime.of(9, 0))
                .set(field(ServiceScheduleEntity::getEndTime), LocalTime.of(10, 0))
                .create();

        // Mock de la jerarquía para evitar NPE en el check de seguridad
        CompanyEntity company = Instancio.of(CompanyEntity.class).set(field(CompanyEntity::getUserId), companyId).create();
        ServiceEntity service = Instancio.of(ServiceEntity.class).set(field(ServiceEntity::getCompany), company).create();
        scheduleEntity.setService(service);

        when(serviceScheduleService.findById(scheduleId)).thenReturn(Optional.of(scheduleEntity));
        when(serviceScheduleService.findAllByServiceAndDayOfWeekIn(any(), anyList())).thenReturn(List.of(scheduleEntity));

        // Act
        // Enviamos dayOfWeek y endTime con valor para cubrir el 1er y 3er if
        patchScheduleUseCase.patchSchedule(
                scheduleId,
                companyId,
                JsonNullable.of(newDay),
                JsonNullable.undefined(), // El 2do if se lo salta (ya tendrá coverage de otros tests)
                JsonNullable.of(newEndTime)
        );

        // Assert
        assertEquals(newDay, scheduleEntity.getDayOfWeek(), "El dayOfWeek debería haberse actualizado");
        assertEquals(newEndTime, scheduleEntity.getEndTime(), "El endTime debería haberse actualizado");
        verify(serviceScheduleMapper).toScheduleSummary(scheduleEntity);
    }

    @Test
    void patchSchedule_shouldUpdateAllFields_whenAllPresent() {
        // Arrange
        UUID companyId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        Integer newDay = DayOfWeek.WEDNESDAY.getValue();
        LocalTime newStart = LocalTime.of(11, 0);
        LocalTime newEnd = LocalTime.of(12, 0);

        ServiceScheduleEntity scheduleEntity = Instancio.of(ServiceScheduleEntity.class)
                .set(field(ServiceScheduleEntity::getId), scheduleId)
                .create();

        CompanyEntity company = Instancio.of(CompanyEntity.class).set(field(CompanyEntity::getUserId), companyId).create();
        ServiceEntity service = Instancio.of(ServiceEntity.class).set(field(ServiceEntity::getCompany), company).create();
        scheduleEntity.setService(service);

        when(serviceScheduleService.findById(scheduleId)).thenReturn(Optional.of(scheduleEntity));
        when(serviceScheduleService.findAllByServiceAndDayOfWeekIn(any(), anyList())).thenReturn(List.of(scheduleEntity));

        // Act
        patchScheduleUseCase.patchSchedule(
                scheduleId,
                companyId,
                JsonNullable.of(newDay),
                JsonNullable.of(newStart),
                JsonNullable.of(newEnd)
        );

        // Assert
        assertEquals(newDay, scheduleEntity.getDayOfWeek());
        assertEquals(newStart, scheduleEntity.getStartTime());
        assertEquals(newEnd, scheduleEntity.getEndTime());
    }

}