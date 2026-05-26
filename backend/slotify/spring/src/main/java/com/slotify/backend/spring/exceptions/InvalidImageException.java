package com.slotify.backend.spring.exceptions;

import lombok.Getter;

@Getter
public class InvalidImageException extends RuntimeException {

    public InvalidImageException(String message) {
        super(message);
    }
}
