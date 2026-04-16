package com.slotify.backend.spring.service.controllers;

import com.slotify.backend.spring.service.dtos.CreateServiceRequest;
import com.slotify.backend.spring.service.dtos.CreateServiceResponse;
import com.slotify.backend.spring.service.useCases.CreateServiceUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
public class ServiceController {

    private final CreateServiceUseCase createServiceUseCase;

    @PostMapping()
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CreateServiceResponse> createService(
            @AuthenticationPrincipal Jwt jwt,
            @Valid CreateServiceRequest createServiceRequest
    ) {
        CreateServiceResponse createServiceResponse = this.createServiceUseCase.createService(
                UUID.fromString(jwt.getSubject()),
                createServiceRequest.getServiceName(),
                createServiceRequest.getMinutesDuration(),
                createServiceRequest.getPriceCent()
        );

        return ResponseEntity.created(URI.create("/service/" + createServiceResponse.getServiceId()))
                .body(createServiceResponse);
    }
}
