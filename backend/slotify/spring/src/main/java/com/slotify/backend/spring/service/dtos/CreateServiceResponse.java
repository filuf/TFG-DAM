package com.slotify.backend.spring.service.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Builder
@Getter
@ToString
public class CreateServiceResponse {

    private UUID serviceId;
    private String serviceName;
    private int minutesDuration;
    private int priceCent;
    private String description;
}
