package com.slotify.backend.spring.user.services;

import com.slotify.backend.spring.user.models.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    public UserEntity saveUser(UserEntity userEntity);

    Optional<UserEntity> findUserById(UUID userId);
}
