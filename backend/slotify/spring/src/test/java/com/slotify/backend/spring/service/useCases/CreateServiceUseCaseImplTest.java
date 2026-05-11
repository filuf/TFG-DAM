package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.company.services.CompanyService;
import com.slotify.backend.spring.service.dtos.CreateServiceResponse;
import com.slotify.backend.spring.service.mappers.ServiceMapper;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.services.ServiceService;
import jakarta.persistence.EntityNotFoundException;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateServiceUseCaseImplTest {

    private CreateServiceUseCaseImpl createServiceUseCase;

    @Mock
    private ServiceMapper serviceMapper;
    @Mock
    private ServiceService serviceService;
    @Mock
    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        this.createServiceUseCase = new CreateServiceUseCaseImpl(
                serviceMapper,
                serviceService,
                companyService
        );
    }

    @Test
    void createService_whenCompanyDoesNotExist_shouldThrowEntityNotFoundException() {
        // Arrange
        UUID companyId = UUID.randomUUID();
        when(companyService.findCompanyById(companyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () ->
                createServiceUseCase.createService(companyId, "Corte de pelo", 30, 1500, "Descripción")
        );

        verifyNoInteractions(serviceMapper);
        verifyNoInteractions(serviceService);
    }

    @Test
    void createService_whenDataIsValid_shouldReturnResponse() {
        // Arrange
        UUID companyId = UUID.randomUUID();
        String name = "Corte de pelo";
        Integer duration = 30;
        Integer price = 2000;
        String desc = "Un corte con estilo";

        // Creamos la empresa con Instancio (por si no hay setters)
        CompanyEntity companyEntity = Instancio.of(CompanyEntity.class)
                .set(field(CompanyEntity::getUserId), companyId)
                .create();

        ServiceEntity serviceEntity = Instancio.create(ServiceEntity.class);
        CreateServiceResponse expectedResponse = Instancio.create(CreateServiceResponse.class);

        // Mocks de comportamiento
        when(companyService.findCompanyById(companyId)).thenReturn(Optional.of(companyEntity));

        when(serviceMapper.toEntity(companyEntity, name, duration, price, desc))
                .thenReturn(serviceEntity);

        when(serviceService.saveService(serviceEntity))
                .thenReturn(serviceEntity);

        when(serviceMapper.toCreateServiceResponse(serviceEntity))
                .thenReturn(expectedResponse);

        // Act
        CreateServiceResponse actualResponse = createServiceUseCase.createService(companyId, name, duration, price, desc);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);

        // Verificaciones de interacción
        verify(companyService).findCompanyById(companyId);
        verify(serviceMapper).toEntity(any(), anyString(), anyInt(), anyInt(), anyString());
        verify(serviceService).saveService(serviceEntity);
        verify(serviceMapper).toCreateServiceResponse(serviceEntity);
    }



}