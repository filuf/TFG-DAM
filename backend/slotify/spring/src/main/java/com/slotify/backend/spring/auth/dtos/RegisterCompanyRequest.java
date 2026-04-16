package com.slotify.backend.spring.auth.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterCompanyRequest {
    @NotBlank
    @Size(max = 200)
    private String companyName;

    @NotBlank
    @Size(min = 10, max = 16)
    private String password;

    @Email
    @Size(max = 320)
    private String emailAddress;

    @Min(1)
    private Integer defaultMaxConcurrentServices;

    @NotBlank
    @Size(max = 300)
    private String physicalAddress;

}
