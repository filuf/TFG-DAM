package com.slotify.backend.spring.reserve.components.fetch.impl;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.reserve.components.fetch.ReserveFetcher;
import com.slotify.backend.spring.reserve.dtos.ReserveSummary;
import com.slotify.backend.spring.reserve.dtos.UserReserveSummary;
import com.slotify.backend.spring.reserve.enums.ReserveFetchType;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import com.slotify.backend.spring.reserve.services.ReserveService;
import com.slotify.backend.spring.service.models.ServiceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserReserveFetcher implements ReserveFetcher<UserReserveSummary> {

    private final ReserveService reserveService;

    @Override
    public Page<UserReserveSummary> fetch(UUID accountId, ReserveFetchType reserveFetchType, Pageable pageable) {
        Page<ReserveEntity> reserves = this.reserveService.findAllReservesByAccountIdAndAccountTypeAndFetchType(
                accountId,
                AccountType.USER,
                reserveFetchType,
                pageable
        );

        return reserves.map(entity -> {

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
        });
    }
}
