package com.slotify.backend.spring.reserve.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CreateReserveRequest {

    @NotNull
    LocalDateTime dateTimeReserve;

    @NotNull
    UUID serviceId;

}
