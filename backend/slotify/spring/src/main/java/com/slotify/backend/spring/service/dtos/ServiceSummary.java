package com.slotify.backend.spring.service.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@ToString
public class ServiceSummary {

    private UUID serviceId;
    private String serviceName;
    private Integer serviceMinutesDuration;
    private Integer servicePriceCent;
    private String s3ImageKey;
    private String description;

    private List<ScheduleSummary> schedules;
}
