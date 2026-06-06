package com.slotify.backend.spring.user.useCases;


import com.slotify.backend.spring.auth.enums.AccountType;
import com.slotify.backend.spring.s3.S3Service;
import com.slotify.backend.spring.user.dtos.UserSummary;
import com.slotify.backend.spring.user.mappers.UserMapper;
import com.slotify.backend.spring.user.models.UserEntity;
import com.slotify.backend.spring.user.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserUseCaseImpl implements GetUserUseCase {

    private final UserService userService;
    private final UserMapper userMapper;
    private final S3Service s3Service;
    @Override
    public UserSummary getUser(UUID userId, AccountType accountType, UUID accountId) {

        if (accountType.equals(AccountType.USER) && !userId.equals(accountId)) {
            throw new AccessDeniedException("No posees los permisos necesarios para acceder a este recurso");
        }

        UserEntity user = this.userService.findUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("No existe un usuario en la base de datos con el id: " + userId));

        String s3ImageUrl = this.s3Service.getTemporalUrl(user.getS3ImageKey());

        return this.userMapper.toUserSummary(user, s3ImageUrl);
    }
}
