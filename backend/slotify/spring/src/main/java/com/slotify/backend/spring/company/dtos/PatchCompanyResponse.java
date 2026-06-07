package com.slotify.backend.spring.company.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Builder
@Getter
@ToString
public class PatchCompanyResponse {

    private UUID companyId;
    private Integer defaultMaxConcurrentServices;
    private String companyName;
    private String phoneNumber;
    private String physicalAddress;
    private String s3ImageUrl;
    private String description;
}
