package com.slotify.backend.spring.auth.controllers;

import com.slotify.backend.spring.auth.dtos.RegisterCompanyRequest;
import com.slotify.backend.spring.auth.dtos.RegisterCompanyResponse;
import com.slotify.backend.spring.auth.dtos.RegisterUserRequest;
import com.slotify.backend.spring.auth.dtos.RegisterUserResponse;
import com.slotify.backend.spring.auth.useCases.RegisterCompanyUseCase;
import com.slotify.backend.spring.auth.useCases.RegisterUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final RegisterCompanyUseCase registerCompanyUseCase;

    @PostMapping("/register/user")
    public ResponseEntity<RegisterUserResponse> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        return ResponseEntity.created(URI.create(""))
                .body(this.registerUserUseCase.registerUser(registerUserRequest));
    }
    @PostMapping("/register/company")
    public ResponseEntity<RegisterCompanyResponse> registerCompany(@Valid @RequestBody RegisterCompanyRequest registerCompanyRequest) {
        return ResponseEntity.created(URI.create(""))
                .body(this.registerCompanyUseCase.registerCompany(registerCompanyRequest));
    }
}
