package com.slotify.backend.spring.user.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
public class UserSummary {

    private UUID userId;
    private String username;
    private String s3ImageUrl;
    private String phoneNumber;
    private String emailAddress;
    private LocalDateTime createdAt;
}
