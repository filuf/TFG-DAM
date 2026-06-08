package com.slotify.backend.spring.reserve.components.cancelation;

import com.slotify.backend.spring.reserve.models.ReserveEntity;

public interface ReserveCancelationCancelator {
    void cancel(ReserveEntity reserve);
}
