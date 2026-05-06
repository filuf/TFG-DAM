package com.slotify.backend.spring.reserve.dtos;

import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
@ToString
@Builder
public class ReservationCanceledEvent {

    private final ReserveEntity reserveEntity;
    private final UUID accountId;
    private final AccountType accountType;
}
