package com.slotify.backend.spring.exceptions;


import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentiasException(BadCredentialsException exc) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(exc.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> error = new HashMap<>();
        error.put("error", "Cuerpo de petición inválido");
        error.put("message", "El body es obligatorio. Asegúrate de enviar un JSON válido.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, List<String>> errors = ex.getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(DefaultMessageSourceResolvable::getDefaultMessage, Collectors.toList())
                ));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<String> handleUnrecognizedPropertyException(UnrecognizedPropertyException exc) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("message: La propiedad " + exc.getPropertyName() + " no está permitida");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException exc) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "La entidad no existe");
        error.put("message", exc.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleAlreadyExistsException(AlreadyExistsException exc) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Conflicto de recursos");
        error.put("message", exc.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ScheduleValidationException.class)
    public ResponseEntity<Map<String, Object>> handleScheduleValidationException(ScheduleValidationException exc) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Conflicto en horario");
        error.put("message", exc.getMessage());
        if (!exc.getOverlaps().isEmpty()) {
            error.put("overlaps", exc.getOverlaps());
        }

        return ResponseEntity.status(exc.getHttpStatus()).body(error);
    }

    @ExceptionHandler(IntervalValidationException.class)
    public ResponseEntity<Map<String, Object>> handleIntervalValidationException(IntervalValidationException exc) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Conflicto en intervalo");
        error.put("message", exc.getMessage());
        exc.getOptOverlap()
                .ifPresent(overlap -> error.put("overlap", overlap));

        return ResponseEntity.status(exc.getHttpStatus()).body(error);
    }

    @ExceptionHandler(ReservationBadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleReservationBadRequestException(ReservationBadRequestException exc) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "error en los datos en reserva");
        error.put("message", exc.getMessage());

        return ResponseEntity.status(exc.getHttpStatus()).body(error);
    }
    @ExceptionHandler(ReservationConflictException.class)
    public ResponseEntity<Map<String, Object>> handleReservationConflictException(ReservationConflictException exc) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Conflicto en reserva");
        error.put("message", exc.getMessage());
        exc.getReserveId()
                .ifPresent(reserveId -> error.put("reserveId", reserveId));

        return ResponseEntity.status(exc.getHttpStatus()).body(error);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.warn("Intento de acceso no autorizado: {}", ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", "Prohibido");
        error.put("message", "No tienes los permisos necesarios (Rol insuficiente)");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception exc) {
        String errorId = UUID.randomUUID().toString();

        log.error("Internal error. id error: {}", errorId, exc);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error interno, por favor contacta con el equipo de desarrollo proporcionando este id " + errorId);
    }
}
