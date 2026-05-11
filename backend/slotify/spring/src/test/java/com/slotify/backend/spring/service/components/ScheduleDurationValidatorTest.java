package com.slotify.backend.spring.service.components;

import com.slotify.backend.spring.exceptions.ScheduleValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleDurationValidatorTest {

    private ScheduleDurationValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ScheduleDurationValidator();
    }

    @Test
    void validate_whenDurationIsCorrect_shouldNotThrowException() {
        // Arrange: 1 hora de duración (60 min)
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(11, 0);

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(start, end));
    }

    @Test
    void validate_whenOvernightDurationIsCorrect_shouldNotThrowException() {
        // Arrange: De 23:00 a 01:00 (120 min) - Cubre el bloque duration.isNegative()
        LocalTime start = LocalTime.of(23, 0);
        LocalTime end = LocalTime.of(1, 0);

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(start, end));
    }

    @Test
    void validate_whenDurationTooLong_shouldThrowScheduleValidationException() {
        // Arrange: Suponiendo que el máximo es menor a 20 horas
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(23, 59);

        // Act & Assert
        ScheduleValidationException exception = assertThrows(ScheduleValidationException.class,
                () -> validator.validate(start, end));

        assertTrue(exception.getMessage().contains("más de"));
    }

    @Test
    void validate_whenDurationTooShort_shouldThrowScheduleValidationException() {
        // Arrange: Duración de 1 minuto (asumiendo que el mínimo es > 1)
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(10, 1);

        // Act & Assert
        ScheduleValidationException exception = assertThrows(ScheduleValidationException.class,
                () -> validator.validate(start, end));

        assertTrue(exception.getMessage().contains("menos de"));
    }

    @Test
    void validate_whenTimesAreEqual_shouldBeTreatedAsZeroOrFullDay() {
        // Arrange: 0 minutos (o 24h según el plusDays)
        LocalTime time = LocalTime.of(10, 0);

        // Act & Assert: Dependiendo de los límites, esto debería fallar por ser muy corto (0 min)
        assertThrows(ScheduleValidationException.class, () -> validator.validate(time, time));
    }
}