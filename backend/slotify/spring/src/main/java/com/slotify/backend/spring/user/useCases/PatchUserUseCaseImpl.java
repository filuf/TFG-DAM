package com.slotify.backend.spring.user.useCases;

import com.slotify.backend.spring.s3.ImageFormatValidator;
import com.slotify.backend.spring.s3.S3Service;
import com.slotify.backend.spring.user.dtos.UserSummary;
import com.slotify.backend.spring.user.mappers.UserMapper;
import com.slotify.backend.spring.user.models.UserEntity;
import com.slotify.backend.spring.user.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatchUserUseCaseImpl implements PatchUserUseCase {

    private final UserService userService;
    private final UserMapper userMapper;
    private final ImageFormatValidator imageFormatValidator;
    private final S3Service s3Service;
    @Override
    @Transactional
    public UserSummary patchUser(
            UUID userId,
            MultipartFile file,
            JsonNullable<String> username,
            JsonNullable<String> phoneNumber
    ) throws IOException {

        UserEntity user = this.userService.findUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("No existe un usuario en la base de datos con el id: " + userId));

        this.patchEntity(file, username, phoneNumber, user);

        String s3ImageUrl = this.s3Service.getTemporalUrl(user.getS3ImageKey());

        return this.userMapper.toUserSummary(user, s3ImageUrl);
    }

    private void patchEntity(
            MultipartFile file,
            JsonNullable<String> username,
            JsonNullable<String> phoneNumber,
            UserEntity user
    ) throws IOException {

        if (this.imageFormatValidator.validateImage(file)) {
            String imageKeyName = this.s3Service.generateFileName(file);
            this.s3Service.uploadFile(imageKeyName, file);

            user.setS3ImageKey(imageKeyName);
        }

        if (username.isPresent()) {
            user.setUsername(username.get());
        }

        if (phoneNumber.isPresent()) {
            user.setPhoneNumber(phoneNumber.get());
        }
    }


}
