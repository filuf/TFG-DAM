package com.slotify.backend.spring.service.dtos;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.service.models.ServiceEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
@Builder
public class ServicePatchedEvent {

    private final ServiceEntity serviceEntity;
    private final CompanyEntity companyEntity;
}
