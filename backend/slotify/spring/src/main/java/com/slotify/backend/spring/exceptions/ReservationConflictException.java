package com.slotify.backend.spring.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

@Getter
public class ReservationConflictException extends RuntimeException {

    private HttpStatus httpStatus = HttpStatus.CONFLICT;
    private UUID reserveId = null;

    public ReservationConflictException(String s) {
        super(s);
    }

    public ReservationConflictException(String s, UUID reserveId) {
        super(s);
        this.reserveId = reserveId;
    }

    public Optional<UUID> getReserveId() {
        return Optional.ofNullable(reserveId);
    }
}
