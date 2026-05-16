package com.slotify.backend.spring.notification.components.fetch.impl;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.notification.components.fetch.NotificationFetchComponentsIf;
import com.slotify.backend.spring.notification.components.fetch.NotificationFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyNotificationFetchComponents implements NotificationFetchComponentsIf {

    private final CompanyNotificationFetcher companyNotificationFetcher;

    @Override
    public AccountType supports() {
        return AccountType.COMPANY;
    }

    @Override
    public NotificationFetcher fetcher() {
        return this.companyNotificationFetcher;
    }
}
