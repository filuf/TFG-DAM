package com.slotify.backend.spring.reserve.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@ToString
public class CreateReserveResponse {
    private UUID reserveId;

    private LocalDateTime reserveDateTime;

    private String companyName;

    private String serviceName;

}
