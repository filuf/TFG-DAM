package com.slotify.backend.spring.exceptions;


import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationException(WebExchangeBindException exc) {
        Map<String, List<String>> errors = exc.getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(DefaultMessageSourceResolvable::getDefaultMessage, Collectors.toList())
                ));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<String> handleUnrecognizedPropertyException(UnrecognizedPropertyException exc) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("La propiedad " + exc.getPropertyName() + " no está permitida");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception exc) {
        String errorId = UUID.randomUUID().toString();

        log.error("Internal error, id error: {}", errorId, exc);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error interno, por favor contacta con el equipo de desarrollo proporcionando este id " + errorId);
    }
}
