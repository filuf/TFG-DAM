package com.slotify.backend.spring.user.services;

import com.slotify.backend.spring.user.models.UserEntity;

public interface UserService {

    public UserEntity saveUser(UserEntity userEntity);
}
