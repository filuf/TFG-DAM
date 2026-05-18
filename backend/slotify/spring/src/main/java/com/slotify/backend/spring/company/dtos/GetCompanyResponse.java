package com.slotify.backend.spring.company.dtos;

import com.slotify.backend.spring.service.dtos.ServiceSummary;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@ToString
public class GetCompanyResponse {

    private UUID companyId;
    private Integer defaultMaxConcurrentServices;
    private String companyName;
    private String phoneNumber;
    private String emailAddress;
    private String physicalAddress;
    private String s3ImageKey;
    private String description;
    private Short rattingAvg;

    List<ServiceSummary> services;
}
