package com.slotify.backend.spring.auth.useCases;

import com.slotify.backend.spring.auth.dtos.RegisterUserRequest;
import com.slotify.backend.spring.auth.dtos.RegisterUserResponse;
import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.auth.mappers.RegisterUserMapper;
import com.slotify.backend.spring.auth.services.KeycloakService;
import com.slotify.backend.spring.user.models.UserEntity;
import com.slotify.backend.spring.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserService userService;
    private final KeycloakService keycloakService;
    private final RegisterUserMapper registerUserMapper;

    @Override
    @Transactional
    public RegisterUserResponse registerUser(RegisterUserRequest request) {

        String username = request.getUsername();
        String email = request.getEmailAddress();

        UUID userId = this.keycloakService.createUser(username, email, request.getPassword(), AccountType.USER);

        UserEntity userEntity = UserEntity.builder()
                .userId(userId)
                .username(username)
                .createdAt(LocalDateTime.now())
                .emailAddress(email)
                .build();
        userEntity = this.userService.saveUser(userEntity);

        return this.registerUserMapper.toResponse(userEntity);


    }
}
