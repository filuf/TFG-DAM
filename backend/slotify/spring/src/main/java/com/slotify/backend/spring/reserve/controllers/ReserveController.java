package com.slotify.backend.spring.reserve.controllers;

import com.slotify.backend.spring.reserve.dtos.CreateReserveRequest;
import com.slotify.backend.spring.reserve.dtos.CreateReserveResponse;
import com.slotify.backend.spring.reserve.useCases.CreateReserveUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reserves")
public class ReserveController {

    private final CreateReserveUseCase createReserveUseCase;
    @PostMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CreateReserveResponse> createReserve(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateReserveRequest request
    ) {
        CreateReserveResponse response = this.createReserveUseCase.createReserve(
                UUID.fromString(jwt.getSubject()),
                request.getServiceId(),
                request.getDateTimeReserve()
        );
        return ResponseEntity.created(URI.create("/reserves/" + response.getReserveId()))
                .body(response);
    }

}
