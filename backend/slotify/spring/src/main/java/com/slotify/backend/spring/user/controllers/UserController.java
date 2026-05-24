package com.slotify.backend.spring.user.controllers;

import com.slotify.backend.spring.user.dtos.PatchUserRequest;
import com.slotify.backend.spring.user.dtos.UserSummary;
import com.slotify.backend.spring.user.useCases.PatchUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final PatchUserUseCase patchUserUseCase;

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserSummary> patchUser(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("request") @Valid PatchUserRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) throws IOException {
        UserSummary userSummary = this.patchUserUseCase.patchUser(
                UUID.fromString(jwt.getSubject()),
                file,
                request.getUsernameJsonNullable(),
                request.getPhoneNumberJsonNullable()
        );

        return ResponseEntity.ok(userSummary);
    }
}
