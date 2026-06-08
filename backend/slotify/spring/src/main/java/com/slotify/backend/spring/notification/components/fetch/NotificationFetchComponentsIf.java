package com.slotify.backend.spring.notification.components.fetch;

import com.slotify.backend.spring.auth.enums.AccountType;

public interface NotificationFetchComponentsIf {

    AccountType supports();

    NotificationFetcher fetcher();
}
