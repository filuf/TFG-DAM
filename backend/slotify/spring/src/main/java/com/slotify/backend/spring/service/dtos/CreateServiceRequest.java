package com.slotify.backend.spring.service.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateServiceRequest {

    @NotBlank
    @Size(max = 45)
    private String serviceName;

    @Min(5)
    @Max(480)
    private Integer minutesDuration;

    @Min(100)
    @Max(1000000)
    private Integer priceCent;
}
