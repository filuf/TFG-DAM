package com.slotify.backend.spring.service.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CreateServiceRequest {

    @NotBlank
    @Size(max = 45)
    private String serviceName;

    @Min(5)
    @Max(480)
    @NotNull
    private Integer minutesDuration;

    @Min(100)
    @Max(1000000)
    @NotNull
    private Integer priceCent;

    private String description;
}
