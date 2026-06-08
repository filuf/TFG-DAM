package com.slotify.backend.spring.reserve.components.cancelation;

import com.slotify.backend.spring.auth.enums.AccountType;

public interface CancelationComponentsIf {
    AccountType supports();

    ReserveCancelationValidator validator();

    ReserveCancelationCancelator cancelator();

    ReserveCancelationEventPublisher eventPublisher();
}
