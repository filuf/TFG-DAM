package com.slotify.backend.spring.user.services;

import com.slotify.backend.spring.user.models.UserEntity;
import com.slotify.backend.spring.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserEntity saveUser(UserEntity userEntity) {
        return this.userRepository.save(userEntity);
    }

    @Override
    public Optional<UserEntity> findUserById(UUID userId) {
        return this.userRepository.findById(userId);
    }
}
