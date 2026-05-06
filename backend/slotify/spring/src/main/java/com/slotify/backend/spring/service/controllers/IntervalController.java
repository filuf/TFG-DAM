package com.slotify.backend.spring.service.controllers;

import com.slotify.backend.spring.service.usecases.DeleteIntervalUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/intervals")
@RequiredArgsConstructor
public class IntervalController {

    private final DeleteIntervalUseCase deleteIntervalUseCase;

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
