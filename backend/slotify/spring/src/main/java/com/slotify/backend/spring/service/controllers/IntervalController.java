package com.slotify.backend.spring.service.controllers;

import com.slotify.backend.spring.service.useCases.DeleteIntervalUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/intervals")
public class IntervalController {

    private final DeleteIntervalUseCase deleteIntervalUseCase;

    @DeleteMapping("/{intervalId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<Void> deleteInterval(
            @PathVariable UUID intervalId,
            @AuthenticationPrincipal Jwt jwt) {

        UUID companyId = UUID.fromString(jwt.getSubject());
        deleteIntervalUseCase.deleteInterval(companyId, intervalId);
        return ResponseEntity.noContent().build();
    }
}