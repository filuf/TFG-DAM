package com.slotify.backend.spring.auth.services;

import com.slotify.backend.spring.auth.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public interface KeycloakService {
    public UUID createUser(String username, String email, String password, AccountType accountType);
}
