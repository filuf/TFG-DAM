package com.slotify.backend.spring.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterUserRequest {

    @NotBlank
    @Size(max = 70)
    private String username;

    @NotBlank
    @Size(min = 10, max = 16)
    private String password;

    @Email
    @Size(max = 320)
    private String emailAddress;

}
