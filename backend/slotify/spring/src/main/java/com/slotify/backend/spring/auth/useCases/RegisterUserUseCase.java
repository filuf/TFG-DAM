package com.slotify.backend.spring.auth.useCases;

import com.slotify.backend.spring.auth.dtos.RegisterUserRequest;
import com.slotify.backend.spring.auth.dtos.RegisterUserResponse;
import com.slotify.backend.spring.user.models.UserEntity;

public interface RegisterUserUseCase {

    RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest);
}
