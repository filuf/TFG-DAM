package com.slotify.backend.spring.service.controllers;

import com.slotify.backend.spring.service.dtos.PatchScheduleRequest;
import com.slotify.backend.spring.service.dtos.ScheduleSummary;
import com.slotify.backend.spring.service.useCases.PatchScheduleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {

    private final PatchScheduleUseCase patchScheduleUseCase;

    @PreAuthorize("hasRole('COMPANY')")
    @PatchMapping("/{scheduleId}")
    public ResponseEntity<ScheduleSummary> patchSchedule(
            @PathVariable UUID scheduleId,
            @RequestBody PatchScheduleRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        ScheduleSummary response = patchScheduleUseCase.patchSchedule(
                scheduleId,
                UUID.fromString(jwt.getSubject()),
                request.getDayOfWeek(),
                request.getStartTime(),
                request.getEndTime()
        );

        return ResponseEntity.ok(response);
    }
}
