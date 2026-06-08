package com.slotify.backend.spring.reserve.components.cancelation.impl;

import com.slotify.backend.spring.reserve.components.cancelation.ReserveCancelationCancelator;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenericCancelationReserveCancelator implements ReserveCancelationCancelator {
    @Override
    public void cancel(ReserveEntity reserve) {
        reserve.cancelReserve();
    }
}
