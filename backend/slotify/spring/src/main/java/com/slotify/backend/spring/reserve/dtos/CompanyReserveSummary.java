package com.slotify.backend.spring.reserve.dtos;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder
@ToString(callSuper = true)
@Getter
@JsonTypeName("COMPANY")
public class CompanyReserveSummary extends ReserveSummary {

    private UUID userId;

    private String userName;
    private String userImageUrl;

}
