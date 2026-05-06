package com.slotify.backend.spring.company.controllers;

import com.slotify.backend.spring.company.dtos.CreateIntervalRequest;
import com.slotify.backend.spring.company.dtos.CreateIntervalResponse;
import com.slotify.backend.spring.company.useCases.CreateIntervalUseCase;
import com.slotify.backend.spring.company.useCases.DeleteIntervalUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/intervals")
@RequiredArgsConstructor
public class IntervalController {

    private final CreateIntervalUseCase createIntervalUseCase;
    private final DeleteIntervalUseCase deleteIntervalUseCase;

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
