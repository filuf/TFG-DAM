package com.slotify.backend.spring.reserve.components.cancelation.impl;

import com.slotify.backend.spring.reserve.components.cancelation.ReserveCancelationEventPublisher;
import com.slotify.backend.spring.reserve.dtos.ReservationCanceledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyCancelationEventPublisher implements ReserveCancelationEventPublisher {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(ReservationCanceledEvent event) {
        publisher.publishEvent(event);
    }
}
