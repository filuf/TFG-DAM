package com.slotify.backend.spring.company.components;

import com.slotify.backend.spring.exceptions.IntervalValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class IntervalTimeValidator {

    public void validate(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new IntervalValidationException("El intervalo a crear no puede ser anterior a la fecha actual", HttpStatus.BAD_REQUEST);
        }

        if (startDateTime.isAfter(endDateTime) || startDateTime.isEqual(endDateTime)) {
            throw new IntervalValidationException("El intervalo a crear no puede terminar antes o a la vez que su comienzo", HttpStatus.BAD_REQUEST);
        }
    }
}
