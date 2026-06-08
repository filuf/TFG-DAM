package com.slotify.backend.spring.reserve.components.fetch.impl;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.reserve.components.fetch.FetchComponentsIf;
import com.slotify.backend.spring.reserve.components.fetch.ReserveFetcher;
import com.slotify.backend.spring.reserve.dtos.UserReserveSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FetchComponentsUser implements FetchComponentsIf<UserReserveSummary> {

    private final UserReserveFetcher fetcher;
    @Override
    public AccountType supports() {
        return AccountType.USER;
    }

    @Override
    public ReserveFetcher<UserReserveSummary> fetcher() {
        return fetcher;
    }
}
