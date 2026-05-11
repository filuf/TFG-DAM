package com.slotify.backend.spring.service.controllers;

import com.slotify.backend.spring.service.dtos.*;
import com.slotify.backend.spring.service.useCases.CreateServiceScheduleUseCase;
import com.slotify.backend.spring.service.useCases.CreateServiceUseCase;
import com.slotify.backend.spring.service.useCases.GetServiceSchedulesUseCase;
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
    private final GetServiceSchedulesUseCase getServiceSchedulesUseCase;

    @PostMapping()
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CreateServiceResponse> createService(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateServiceRequest createServiceRequest
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

    @PreAuthorize("hasAnyRole('USER','COMPANY')")
    @GetMapping("/{serviceId}/schedules")
    public ResponseEntity<ServiceSummary> getServiceSchedules(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID serviceId
    ) {
        ServiceSummary serviceSummary = this.getServiceSchedulesUseCase.getServiceSummary(serviceId);

        return ResponseEntity.ok(serviceSummary);
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
