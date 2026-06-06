package com.slotify.backend.spring.mailer;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromMail;

    /**
     * Envía un mail con los parámetros proporcionados
     *
     * @param request parametros del mail
     * @throws Exception lanza un 500 si la dirección no es válida o el asunto excede los 50 caracteres
     */
    public void sendValidMail(SendMailRequest request) throws Exception {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromMail);

            String sendTo = request.getSendToEmail();
            if (!validateEmailDirection(sendTo)) {
                throw new Exception("El mail proporcionado es invalido");
            }
            helper.setTo(sendTo);

            String subject = request.getSubject();
            if (subject.length() > 50) {
                throw new Exception("El asunto no puede superar los 50 caracteres");
            }
            helper.setSubject(subject);

            // indicamos que es html
            helper.setText(request.getText(), true);

            if (request.getMailAttachment() != null) {
                MailAttachment attachment = request.getMailAttachment();

                helper.addAttachment(
                        attachment.fileName(),
                        new ByteArrayResource(
                                attachment.content().getBytes(StandardCharsets.UTF_8)
                        ),
                        attachment.contentType()
                );
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new Exception("Error al enviar el correo: " + e.getMessage());
        }
    }

    /**
     * Valida si una dirección de mail cumple con una expresión regular extraida de <a href="https://www.baeldung.com/java-email-validation-regex">baeldung</a>
     *
     * @param email email a comprobar
     * @return true si es válido, false si no lo es
     */
    private boolean validateEmailDirection(String email) {
        Pattern pattern = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
        return pattern.matcher(email).matches();
    }
}