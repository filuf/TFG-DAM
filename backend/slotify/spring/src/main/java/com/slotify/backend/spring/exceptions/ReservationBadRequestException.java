package com.slotify.backend.spring.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ReservationBadRequestException extends RuntimeException {

    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public ReservationBadRequestException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ReservationBadRequestException(String message) {
        super(message);
    }
}
