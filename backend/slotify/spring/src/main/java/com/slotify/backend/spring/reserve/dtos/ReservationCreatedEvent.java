package com.slotify.backend.spring.reserve.dtos;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.user.models.UserEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Builder
@AllArgsConstructor
@ToString
public class ReservationCreatedEvent {

    private final ReserveEntity reserveEntity;
    private final UserEntity userEntity;
    private final CompanyEntity companyEntity;
    private final UUID companyId;

    private final String userEmail;
    private final String companyEmail;
    private final String serviceName;
    private final LocalDateTime reserveDateTime;
    private final Integer minutesDuration;

    public static ReservationCreatedEvent from(ReserveEntity reserve, UserEntity userEntity, CompanyEntity companyEntity, ServiceEntity serviceEntity) {
        return ReservationCreatedEvent.builder()
                .reserveEntity(reserve)
                .userEntity(userEntity)
                .companyEntity(companyEntity)
                .companyId(companyEntity.getUserId())
                .userEmail(userEntity.getEmailAddress())
                .companyEmail(companyEntity.getEmailAddress())
                .serviceName(serviceEntity.getServiceName())
                .reserveDateTime(reserve.getServiceTime())
                .minutesDuration(serviceEntity.getServiceMinutesDuration())
                .build();
    }
}
