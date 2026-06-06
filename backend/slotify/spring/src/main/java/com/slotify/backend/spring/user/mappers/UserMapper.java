package com.slotify.backend.spring.user.mappers;

import com.slotify.backend.spring.user.dtos.UserSummary;
import com.slotify.backend.spring.user.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserSummary toUserSummary(UserEntity user, String s3ImageUrl) {
        return UserSummary.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .emailAddress(user.getEmailAddress())
                .phoneNumber(user.getPhoneNumber())
                .s3ImageUrl(s3ImageUrl)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
