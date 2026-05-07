package com.slotify.backend.spring.reserve.components.fetch.impl;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.reserve.components.fetch.ReserveFetcher;
import com.slotify.backend.spring.reserve.dtos.CompanyReserveSummary;
import com.slotify.backend.spring.reserve.enums.ReserveFetchType;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import com.slotify.backend.spring.reserve.services.ReserveService;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.user.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyReserveFetcher implements ReserveFetcher<CompanyReserveSummary> {

    private final ReserveService reserveService;

    @Override
    public Page<CompanyReserveSummary> fetch(UUID accountId, ReserveFetchType reserveFetchType, Pageable pageable) {
        Page<ReserveEntity> allReservesByAccountIdAndAccountTypeAndFetchType = this.reserveService.findAllReservesByAccountIdAndAccountTypeAndFetchType(
                accountId,
                AccountType.COMPANY,
                reserveFetchType,
                pageable
        );

        return allReservesByAccountIdAndAccountTypeAndFetchType.map(entity -> {
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
        });
    }
}
