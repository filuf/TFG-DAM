package com.slotify.backend.spring.auth.enums;

import com.slotify.backend.spring.notification.models.NotificationSender;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AccountType {
    USER("USER"),
    COMPANY("COMPANY");


    private final String type;

    public static AccountType fromType(String type) {
        return Arrays.stream(values())
                .filter(t -> t.type.equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown type: " + type));
    }

    public boolean matches(NotificationSender sender) {
        return this.type.equals(sender.name());
    }
}
