package com.slotify.backend.spring.reserve.mappers;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.reserve.dtos.CompanyReserveSummary;
import com.slotify.backend.spring.reserve.dtos.CreateReserveResponse;
import com.slotify.backend.spring.reserve.dtos.UserReserveSummary;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.user.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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

    public UserReserveSummary getUserReserveSummary(ReserveEntity entity) {

        ServiceEntity service = entity.getService();
        CompanyEntity company = service.getCompany();

        return UserReserveSummary.builder()
                .reserveId(entity.getReserveId())
                .serviceId(service.getServiceId())
                .companyId(company.getUserId())
                .companyName(company.getCompanyName())
                .companyPhisicalAddress(company.getPhysicalAddress())
                .companyImageUrl(company.getS3ImageKey()) // todo: modificar
                .startDateTime(entity.getServiceTime())
                .endDateTime(entity.getServiceTime().plusMinutes(service.getServiceMinutesDuration()))
                .minutesDuration(service.getServiceMinutesDuration())
                .serviceName(service.getServiceName())
                .servicePriceCent(service.getServicePriceCent())
                .isCanceled(entity.isCanceled())
                .build();
    }

    public CompanyReserveSummary getCompanyReserveSummary(ReserveEntity entity) {
        UserEntity user = entity.getUser();
        ServiceEntity service = entity.getService();

        return CompanyReserveSummary.builder()
                .reserveId(entity.getReserveId())
                .serviceId(service.getServiceId())
                .userId(user.getUserId())
                .userName(user.getUsername())
                .userImageUrl(user.getS3ImageKey()) // todo: cambiar
                .startDateTime(entity.getServiceTime())
                .endDateTime(entity.getServiceTime().plusMinutes(service.getServiceMinutesDuration()))
                .minutesDuration(service.getServiceMinutesDuration())
                .serviceName(service.getServiceName())
                .servicePriceCent(service.getServicePriceCent())
                .isCanceled(entity.isCanceled())
                .build();
    }
}
