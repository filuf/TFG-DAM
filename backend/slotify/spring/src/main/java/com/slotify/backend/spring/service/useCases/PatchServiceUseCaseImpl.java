package com.slotify.backend.spring.service.useCases;

import com.slotify.backend.spring.s3.ImageFormatValidator;
import com.slotify.backend.spring.s3.S3Service;
import com.slotify.backend.spring.service.dtos.ServiceSummary;
import com.slotify.backend.spring.service.mappers.ServiceMapper;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.service.services.ServiceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatchServiceUseCaseImpl implements PatchServiceUseCase {

    private final ServiceService serviceService;
    private final ServiceMapper serviceMapper;
    private final ImageFormatValidator imageFormatValidator;
    private final S3Service s3Service;

    @Override
    @Transactional
    public ServiceSummary patchService(
            UUID serviceId,
            UUID companyId,
            MultipartFile file,
            JsonNullable<String> serviceName,
            JsonNullable<Integer> minutesDuration,
            JsonNullable<Integer> priceCent,
            JsonNullable<String> description
    ) throws IOException {
        log.info("La empresa: {}, intenta editar el servicio: {}", companyId, serviceId);

        ServiceEntity serviceEntity = this.serviceService.findServiceByIdWithCompany(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("No existe un servicio en la base de datos con el id: " + serviceId));

        if (!serviceEntity.getCompany().getUserId().equals(companyId)) {
            log.warn("Una empresa intenta editar un servicio que no le pertenece, empresaMalvada: {}, servicioAfectado: {}",
                    companyId, serviceEntity.getServiceId());
            throw new AccessDeniedException("No posees los permisos necesarios para modificar este recurso");
        }

        patchEntity(file, serviceName, minutesDuration, priceCent, description, serviceEntity);

        return this.serviceMapper.toServiceSummary(serviceEntity);
    }

    private void patchEntity(
            MultipartFile file,
            JsonNullable<String> serviceName,
            JsonNullable<Integer> minutesDuration,
            JsonNullable<Integer> priceCent,
            JsonNullable<String> description,
            ServiceEntity serviceEntity
    ) throws IOException {

        if (this.imageFormatValidator.validateImage(file)) {
            String imageKeyName = this.s3Service.generateFileName(file);
            this.s3Service.uploadFile(imageKeyName, file);

            serviceEntity.setS3ImageKey(imageKeyName);
        }

        if (serviceName.isPresent()) {
            serviceEntity.setServiceName(serviceName.get());
        }

        if (minutesDuration.isPresent()) {
            serviceEntity.setServiceMinutesDuration(minutesDuration.get());
        }

        if (priceCent.isPresent()) {
            serviceEntity.setServicePriceCent(priceCent.get());
        }

        if (description.isPresent()) {
            serviceEntity.setDescription(description.get());
        }
    }
}
