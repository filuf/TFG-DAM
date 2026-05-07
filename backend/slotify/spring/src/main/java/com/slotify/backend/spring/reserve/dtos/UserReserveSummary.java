package com.slotify.backend.spring.reserve.dtos;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder
@ToString(callSuper = true)
@Getter
@JsonTypeName("USER")
public class UserReserveSummary extends ReserveSummary {

    private UUID companyId;

    private String companyName;
    private String companyPhisicalAddress;
    private String companyImageUrl;

}
