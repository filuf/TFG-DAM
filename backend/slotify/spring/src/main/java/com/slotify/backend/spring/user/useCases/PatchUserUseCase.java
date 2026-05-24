package com.slotify.backend.spring.user.useCases;

import com.slotify.backend.spring.user.dtos.UserSummary;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface PatchUserUseCase {

    UserSummary patchUser(UUID userId, MultipartFile file, JsonNullable<String> username, JsonNullable<String> phoneNumber) throws IOException;
}
