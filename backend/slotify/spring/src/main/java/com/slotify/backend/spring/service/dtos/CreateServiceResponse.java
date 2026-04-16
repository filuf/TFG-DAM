package com.slotify.backend.spring.service.dtos;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class CreateServiceResponse {

    private UUID serviceId;
    private String serviceName;
    private int minutesDuration;
    private int priceCent;
}
