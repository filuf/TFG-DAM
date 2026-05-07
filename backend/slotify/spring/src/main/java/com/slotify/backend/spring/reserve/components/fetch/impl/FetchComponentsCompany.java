package com.slotify.backend.spring.reserve.components.fetch.impl;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.reserve.components.fetch.FetchComponentsIf;
import com.slotify.backend.spring.reserve.components.fetch.ReserveFetcher;
import com.slotify.backend.spring.reserve.dtos.CompanyReserveSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FetchComponentsCompany implements FetchComponentsIf<CompanyReserveSummary> {

    private final CompanyReserveFetcher companyReserveFetcher;

    @Override
    public AccountType supports() {
        return AccountType.COMPANY;
    }

    @Override
    public ReserveFetcher<CompanyReserveSummary> fetcher() {
        return companyReserveFetcher;
    }
}
