package com.slotify.backend.spring.company.controllers;

import com.slotify.backend.spring.company.dtos.CreateIntervalRequest;
import com.slotify.backend.spring.company.dtos.CreateIntervalResponse;
import com.slotify.backend.spring.company.dtos.IntervalSummary;
import com.slotify.backend.spring.company.dtos.UpdateIntervalRequest;
import com.slotify.backend.spring.company.dtos.UpdateIntervalResponse;
import com.slotify.backend.spring.company.enums.IntervalFetchMode;
import com.slotify.backend.spring.company.useCases.CreateIntervalUseCase;
import com.slotify.backend.spring.company.useCases.DeleteIntervalUseCase;
import com.slotify.backend.spring.company.useCases.GetIntervalsUseCase;
import com.slotify.backend.spring.company.useCases.UpdateIntervalUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/intervals")
@RequiredArgsConstructor
public class IntervalController {

    private final CreateIntervalUseCase createIntervalUseCase;
    private final DeleteIntervalUseCase deleteIntervalUseCase;
    private final GetIntervalsUseCase getIntervalsUseCase;
    private final UpdateIntervalUseCase updateIntervalUseCase;

    @GetMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<IntervalSummary>> getIntervals(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "PRESENT") IntervalFetchMode fetchMode
    ) {
        List<IntervalSummary> intervals = this.getIntervalsUseCase.getIntervals(
                UUID.fromString(jwt.getSubject()),
                fetchMode
        );

        if (intervals.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(intervals);
    }

    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CreateIntervalResponse> createInterval (
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateIntervalRequest request
    ) {
        CreateIntervalResponse response = this.createIntervalUseCase.createInterval(
                UUID.fromString(jwt.getSubject()),
                request.getMaxConcurrentService(),
                request.getStartDateTime(),
                request.getEndDateTime()
        );

        return ResponseEntity.created(URI.create("/intervals/" + response.getIntervalId()))
                .body(response);
    }

    @PutMapping("/{intervalId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<UpdateIntervalResponse> updateInterval(
            @PathVariable UUID intervalId,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateIntervalRequest request
    ) {
        UpdateIntervalResponse response = this.updateIntervalUseCase.updateInterval(
                UUID.fromString(jwt.getSubject()),
                intervalId,
                request.getMaxConcurrentService(),
                request.getStartDateTime(),
                request.getEndDateTime()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{intervalId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<Void> deleteInterval(
            @PathVariable UUID intervalId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        this.deleteIntervalUseCase.deleteInterval(
                UUID.fromString(jwt.getSubject()),
                intervalId
        );

        return ResponseEntity.noContent().build();
    }
}
