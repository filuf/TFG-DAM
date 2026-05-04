package com.slotify.backend.spring.reserve.mappers;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.reserve.dtos.CreateReserveResponse;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.user.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReserveMapper {

    public CreateReserveResponse toCreateReserveResponse(LocalDateTime reserveDateTime, ReserveEntity reserveEntity, ServiceEntity serviceEntity, CompanyEntity company) {
        return CreateReserveResponse.builder()
                .reserveId(reserveEntity.getReserveId())
                .serviceName(serviceEntity.getServiceName())
                .companyName(company.getCompanyName())
                .reserveDateTime(reserveDateTime)
                .build();
    }

    public ReserveEntity toEntity(UserEntity userEntity, ServiceEntity serviceEntity, LocalDateTime reserveDateTime, LocalDateTime createdAt, Boolean isCanceled) {
        return ReserveEntity.builder()
                .user(userEntity)
                .service(serviceEntity)
                .serviceTime(reserveDateTime)
                .createdAt(createdAt)
                .isCanceled(isCanceled)
                .build();
    }
}
