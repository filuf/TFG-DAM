package com.slotify.backend.spring.mailer;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class SendMailRequest {

    private String sendToEmail;
    private String subject;
    private String text;
}