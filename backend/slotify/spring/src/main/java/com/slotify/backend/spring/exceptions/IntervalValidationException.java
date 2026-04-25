package com.slotify.backend.spring.exceptions;

import com.slotify.backend.spring.company.models.CompanyIntervalEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@Getter
public class IntervalValidationException extends RuntimeException {

    private CompanyIntervalEntity overlap;
    private HttpStatus httpStatus = HttpStatus.CONFLICT;

    public IntervalValidationException(String message) {
        super(message);
    }

    public IntervalValidationException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public IntervalValidationException(String message, CompanyIntervalEntity overlap, HttpStatus httpStatus) {
        super(message);
        this.overlap = overlap;
        this.httpStatus = httpStatus;
    }

    public Optional<CompanyIntervalEntity> getOptOverlap() {
        return Optional.ofNullable(overlap);
    }
}
