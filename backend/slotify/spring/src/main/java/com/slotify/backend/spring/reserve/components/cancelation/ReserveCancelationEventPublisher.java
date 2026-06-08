package com.slotify.backend.spring.reserve.components.cancelation;

import com.slotify.backend.spring.reserve.dtos.ReservationCanceledEvent;

public interface ReserveCancelationEventPublisher {

    void publish(ReservationCanceledEvent event);
}
