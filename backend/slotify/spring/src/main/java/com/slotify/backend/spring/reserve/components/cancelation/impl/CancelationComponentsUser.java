package com.slotify.backend.spring.reserve.components.cancelation.impl;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.reserve.components.cancelation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelationComponentsUser implements CancelationComponentsIf {

    private final UserCancelationReserveValidator validator;
    private final UserCancelationReserveEventPublisher eventPublisher;
    private final GenericCancelationReserveCancelator cancelator;
    @Override
    public AccountType supports() {
        return AccountType.USER;
    }

    @Override
    public ReserveCancelationValidator validator() {
        return validator;
    }

    @Override
    public ReserveCancelationCancelator cancelator() {
        return cancelator;
    }


    @Override
    public ReserveCancelationEventPublisher eventPublisher() {
        return eventPublisher;
    }
}
