package com.slotify.backend.spring.user.repositories;

import com.slotify.backend.spring.user.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
}
