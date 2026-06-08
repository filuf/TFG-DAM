package com.slotify.backend.spring.reserve.components.cancelation.impl;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.reserve.components.cancelation.CancelationComponentsIf;
import com.slotify.backend.spring.reserve.components.cancelation.ReserveCancelationCancelator;
import com.slotify.backend.spring.reserve.components.cancelation.ReserveCancelationEventPublisher;
import com.slotify.backend.spring.reserve.components.cancelation.ReserveCancelationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelationComponentsCompany implements CancelationComponentsIf {

    private final CompanyCancelationReserveValidator validator;
    private final GenericCancelationReserveCancelator cancelator;
    private final CompanyCancelationEventPublisher eventPublisher;
    @Override
    public AccountType supports() {
        return AccountType.COMPANY;
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
