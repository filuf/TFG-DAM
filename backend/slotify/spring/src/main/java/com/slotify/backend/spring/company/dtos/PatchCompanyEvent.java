package com.slotify.backend.spring.company.dtos;

import com.slotify.backend.spring.company.models.CompanyEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;


@Getter
@RequiredArgsConstructor
@ToString
@Builder
public class PatchCompanyEvent {
    private final CompanyEntity companyEntity;
}
