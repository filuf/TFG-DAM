package com.slotify.backend.spring.service.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleOverlapValidatorTest {

        private ScheduleOverlapValidator validator;

        @BeforeEach
        void setUp() {
            validator = new ScheduleOverlapValidator();
        }

        @Test
        void overlaps_sameDayDirectOverlap_shouldReturnTrue() {
            // Rango 1: Lunes 10:00 - 12:00
            // Rango 2: Lunes 11:00 - 13:00 (Solapa por 1 hora)
            boolean result = validator.overlaps(
                    DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0),
                    DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(13, 0)
            );

            assertTrue(result);
        }

        @Test
        void overlaps_noOverlapSameDay_shouldReturnFalse() {
            // Rango 1: Martes 08:00 - 09:00
            // Rango 2: Martes 09:00 - 10:00 (Tocarse en el borde NO es solapar según start1 < end2)
            boolean result = validator.overlaps(
                    DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(9, 0),
                    DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(10, 0)
            );

            assertFalse(result);
        }

        @Test
        void overlaps_nightShiftOverlapping_shouldReturnTrue() {
            // Rango 1: Lunes 23:00 - Martes 02:00 (Cruza medianoche)
            // Rango 2: Martes 01:00 - 03:00
            boolean result = validator.overlaps(
                    DayOfWeek.MONDAY, LocalTime.of(23, 0), LocalTime.of(2, 0),
                    DayOfWeek.TUESDAY, LocalTime.of(1, 0), LocalTime.of(3, 0)
            );

            assertTrue(result);
        }

        @Test
        void overlaps_weekEndWrapAround_shouldReturnTrue() {
            // CASO CRÍTICO: Domingo noche cruzando a Lunes mañana
            // Rango 1: Domingo 23:00 - Lunes 02:00
            // Rango 2: Lunes 01:00 - 03:00
            boolean result = validator.overlaps(
                    DayOfWeek.SUNDAY, LocalTime.of(23, 0), LocalTime.of(2, 0),
                    DayOfWeek.MONDAY, LocalTime.of(1, 0), LocalTime.of(3, 0)
            );

            assertTrue(result);
        }

        @Test
        void overlaps_reverseWeekEndWrapAround_shouldReturnTrue() {
            // Igual que el anterior pero invirtiendo los parámetros (el lunes se compara con el domingo)
            boolean result = validator.overlaps(
                    DayOfWeek.MONDAY, LocalTime.of(1, 0), LocalTime.of(3, 0),
                    DayOfWeek.SUNDAY, LocalTime.of(23, 0), LocalTime.of(2, 0)
            );

            assertTrue(result);
        }

        @ParameterizedTest
        @CsvSource({
                "MONDAY, 10:00, 11:00, TUESDAY, 10:00, 11:00, false", // Distintos días
                "MONDAY, 09:00, 10:00, MONDAY, 08:00, 11:00, true",  // Uno contiene al otro
                "SUNDAY, 22:00, 23:59, SUNDAY, 23:58, 01:00, true",  // Solape al borde del fin de semana
                "FRIDAY, 15:00, 16:00, FRIDAY, 16:01, 17:00, false"  // Por un minuto no solapan
        })
        void overlaps_parameterizedTests(String d1, String s1, String e1, String d2, String s2, String e2, boolean expected) {
            assertEquals(expected, validator.overlaps(
                    DayOfWeek.valueOf(d1), LocalTime.parse(s1), LocalTime.parse(e1),
                    DayOfWeek.valueOf(d2), LocalTime.parse(s2), LocalTime.parse(e2)
            ));
        }

}