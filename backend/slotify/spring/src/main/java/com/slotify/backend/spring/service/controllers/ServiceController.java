package com.slotify.backend.spring.service.controllers;

import com.slotify.backend.spring.service.dtos.CreateServiceRequest;
import com.slotify.backend.spring.service.dtos.CreateServiceResponse;
import com.slotify.backend.spring.service.dtos.CreateServiceScheduleRequest;
import com.slotify.backend.spring.service.dtos.CreateServiceScheduleResponse;
import com.slotify.backend.spring.service.useCases.CreateServiceScheduleUseCase;
import com.slotify.backend.spring.service.useCases.CreateServiceUseCase;
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
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final CreateServiceUseCase createServiceUseCase;
    private final CreateServiceScheduleUseCase createServiceScheduleUseCase;

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
                createServiceRequest.getPriceCent(),
                createServiceRequest.getDescription()
        );

        return ResponseEntity.created(URI.create("/service/" + createServiceResponse.getServiceId()))
                .body(createServiceResponse);
    }


    @PreAuthorize("hasRole('COMPANY')")
    @PostMapping("/{serviceId}/schedules")
    public ResponseEntity<CreateServiceScheduleResponse> createServiceSchedule(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID serviceId,
            @RequestBody @Valid CreateServiceScheduleRequest createServiceScheduleRequest
    ) {
        CreateServiceScheduleResponse createServiceScheduleResponse = this.createServiceScheduleUseCase.createSchedule(
                UUID.fromString(jwt.getSubject()),
                serviceId,
                createServiceScheduleRequest.getDayOfWeek(),
                createServiceScheduleRequest.getStartTime(),
                createServiceScheduleRequest.getEndTime()
        );

        return ResponseEntity.created(URI.create("/service/" + serviceId + "/schedule/" + createServiceScheduleResponse.getScheduleId()))
                .body(createServiceScheduleResponse);
    }

}
