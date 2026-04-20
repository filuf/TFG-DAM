package com.slotify.backend.spring.auth.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Builder
@Getter
@ToString
public class RegisterCompanyResponse {

    private UUID userId;
    private String username;
    private String email;
    private String physicalAddress;
}
