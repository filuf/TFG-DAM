package com.slotify.backend.spring.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ScheduleValidationException extends RuntimeException {

    private List<String> overlaps = new ArrayList<>();
    private HttpStatus httpStatus = HttpStatus.CONFLICT;
    public ScheduleValidationException(String message) {super(message);}
    public ScheduleValidationException(String message, List<String> overlaps) {
        super(message);
        this.overlaps = overlaps;
    }

    public ScheduleValidationException(String message, List<String> overlaps, HttpStatus httpStatus) {
        super(message);
        this.overlaps = overlaps;
        this.httpStatus = httpStatus;
    }

    public ScheduleValidationException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
