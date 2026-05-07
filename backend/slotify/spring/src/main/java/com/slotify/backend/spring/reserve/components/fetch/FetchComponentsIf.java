package com.slotify.backend.spring.reserve.components.fetch;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.reserve.dtos.ReserveSummary;


public interface FetchComponentsIf<T extends ReserveSummary> {

    AccountType supports();

    ReserveFetcher<T> fetcher();

}
