package com.slotify.backend.spring.auth.mappers;

import com.slotify.backend.spring.auth.dtos.RegisterUserResponse;
import com.slotify.backend.spring.user.models.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class RegisterUserMapper {

    public RegisterUserResponse toResponse(UserEntity userEntity) {
        return RegisterUserResponse.builder()
                .userId(userEntity.getUserId())
                .email(userEntity.getEmailAddress())
                .username(userEntity.getUsername())
                .build();
    }
}
