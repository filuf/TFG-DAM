package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.models.ServiceScheduleEntity;
import com.slotify.backend.spring.service.services.ServiceScheduleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteScheduleUseCaseImpl implements DeleteScheduleUseCase {

    private final ServiceScheduleService serviceScheduleService;
    @Override
    public void deleteSchedule(UUID scheduleId, UUID companyId) {

        ServiceScheduleEntity scheduleEntity = this.serviceScheduleService.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("No existe un horario en la base de datos con el id: " + scheduleId));

        ServiceEntity serviceEntity = scheduleEntity.getService();
        if (!serviceEntity.getCompany().getUserId().equals(companyId)) {
            log.warn("Un usuario de otra empresa intenta alternar un horario de forma ilegal, usuarioMalvado: {}, empresaAtacada: {}, scheduleAfectado: {}",
                    companyId, serviceEntity.getCompany().getCompanyName(), scheduleEntity.getId());
            throw new AccessDeniedException("No posees los permisos necesarios para modificar este recurso");
        }

        this.serviceScheduleService.deleteIntervalById(scheduleId);
    }
}
