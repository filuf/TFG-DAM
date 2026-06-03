package com.slotify.backend.spring.mailer;

public record MailAttachment(
        String fileName,
        String content,
        String contentType
) {}