package com.slotify.backend.spring.reserve.events;

import com.slotify.backend.spring.mailer.MailAttachment;
import com.slotify.backend.spring.mailer.MailService;
import com.slotify.backend.spring.mailer.SendMailRequest;
import com.slotify.backend.spring.notification.models.NotificationEntity;
import com.slotify.backend.spring.notification.models.NotificationSender;
import com.slotify.backend.spring.notification.services.NotificationService;
import com.slotify.backend.spring.reserve.dtos.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationCreatedEventListener {

    private final MailService mailService;
    private final NotificationService notificationService;

    @EventListener
    public void saveNotification(ReservationCreatedEvent event) {

        String day = event.getReserveDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String hour = event.getReserveDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        String textContent = String.format("Se ha hecho una reserva del servicio %s para el día %s a la hora %s ",
                event.getServiceName(), day, hour);

        NotificationEntity notification = NotificationEntity.builder()
                .user(event.getUserEntity())
                .company(event.getCompanyEntity())
                .isRead(false)
                .notificationSender(NotificationSender.USER)
                .textContent(textContent)
                .createdAt(LocalDateTime.now())
                .build();

        this.notificationService.saveNotification(notification);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendMails(ReservationCreatedEvent event) throws Exception {

        String icsContent = generateIcs(
                UUID.randomUUID().toString(),
                event.getReserveDateTime(),
                event.getReserveDateTime().plusMinutes(event.getMinutesDuration()),
                event.getServiceName(),
                event.getCompanyEntity().getPhysicalAddress()
        );

        SendMailRequest.SendMailRequestBuilder builder = SendMailRequest.builder()
                .subject("Nueva reserva")
                .mailAttachment(
                        new MailAttachment(
                                "reserva.ics",
                                icsContent,
                                "text/calendar"
                        )
                );

        String price = NumberFormat.getCurrencyInstance(new Locale("es", "ES"))
                .format(event.getReserveEntity().getService().getServicePriceCent() / 100);

        SendMailRequest userRequest = builder.sendToEmail(event.getUserEmail())
                .text(generateUserTemplate(
                        event.getCompanyEntity().getCompanyName(),
                        event.getServiceName(),
                        DateTimeFormatter.ofPattern("dd-MM-yyyy").format(event.getReserveDateTime()),
                        event.getReserveDateTime().toLocalTime().toString(),
                        event.getMinutesDuration().toString(),
                        event.getCompanyEntity().getPhysicalAddress(),
                        price
                )).build();

        this.mailService.sendValidMail(userRequest);

        SendMailRequest companyRequest = builder.sendToEmail(event.getCompanyEmail())
                .text(generateCompanyTemplate(
                        event.getUserEntity().getUsername(),
                        event.getServiceName(),
                        DateTimeFormatter.ofPattern("dd-MM-yyyy").format(event.getReserveDateTime()),
                        event.getReserveDateTime().toLocalTime().toString(),
                        event.getMinutesDuration().toString()
                )).build();

        this.mailService.sendValidMail(companyRequest);

    }

    private String generateIcs(
            String uid,
            LocalDateTime start,
            LocalDateTime end,
            String serviceName,
            String location) {

        ZoneId zone = ZoneId.of("Europe/Madrid");

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

        ZonedDateTime startZoned = start.atZone(zone);
        ZonedDateTime endZoned = end.atZone(zone);
        ZonedDateTime now = ZonedDateTime.now(zone);

        return "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:-//Slotify//Reservas//ES\r\n" +
                "METHOD:REQUEST\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:" + uid + "\r\n" +
                "DTSTAMP:" + now.format(formatter) + "\r\n" +
                "DTSTART;TZID=Europe/Madrid:" + startZoned.format(formatter) + "\r\n" +
                "DTEND;TZID=Europe/Madrid:" + endZoned.format(formatter) + "\r\n" +
                "SUMMARY:" + serviceName + "\r\n" +
                "LOCATION:" + location + "\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR";
    }

    private String generateUserTemplate(String companyName, String service, String date, String time, String minutesDuration, String physicalAddress, String price) {
        return String.format("""
                        <!DOCTYPE html>
                        <html lang="es">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Cancelación de Reserva</title>
                            <style>
                                body {
                                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                                    background-color: #f4f6f8;
                                    margin: 0;
                                    padding: 0;
                                    -webkit-font-smoothing: antialiased;
                                    width: 100%% !important;
                                }
                                .wrapper {
                                    width: 100%%;
                                    table-layout: fixed;
                                    background-color: #f4f6f8;
                                    padding-bottom: 40px;
                                    padding-top: 40px;
                                }
                                .main-table {
                                    background-color: #ffffff;
                                    margin: 0 auto;
                                    width: 100%%;
                                    max-width: 600px;
                                    border-spacing: 0;
                                    font-family: sans-serif;
                                    color: #333333;
                                    border-radius: 8px;
                                    overflow: hidden;
                                    box-shadow: 0 4px 10px rgba(0,0,0,0.05);
                                }
                                .header-strip {
                                    background-color: #198754;
                                    height: 6px;
                                }
                                .content {
                                    padding: 40px 30px;
                                }
                                .title {
                                    font-size: 24px;
                                    font-weight: bold;
                                    color: #1d2124;
                                    margin-bottom: 20px;
                                }
                                .message {
                                    font-size: 16px;
                                    line-height: 1.6;
                                    color: #555555;
                                    margin-bottom: 30px;
                                }
                                .details-box {
                                    background-color: #f8f9fa;
                                    border-left: 4px solid #198754;
                                    padding: 20px;
                                    margin-bottom: 30px;
                                    border-radius: 0 4px 4px 0;
                                }
                                .details-table {
                                    width: 100%%;
                                    border-spacing: 0;
                                }
                                .details-table td {
                                    padding: 6px 0;
                                    font-size: 15px;
                                }
                                .label {
                                    font-weight: bold;
                                    color: #6c757d;
                                    width: 35%%;
                                }
                                .value {
                                    color: #212529;
                                }
                                .footer {
                                    background-color: #f8f9fa;
                                    padding: 20px;
                                    text-align: center;
                                    font-size: 12px;
                                    color: #6c757d;
                                    border-top: 1px solid #eeeeee;
                                }
                            </style>
                        </head>
                        <body>
                                        
                            <center class="wrapper">
                                <table class="main-table" role="presentation">
                                    <!-- Línea de color superior -->
                                    <tr>
                                        <td class="header-strip"></td>
                                    </tr>
                                    <!-- Contenido Principal -->
                                    <tr>
                                        <td class="content">
                                            <h1 class="title">Nueva reserva</h1>
                                            <p class="message">
                                                Hola, gracias por reservar con slotify, te adjuntamos los detalles de tu próxima reserva:
                                            </p>
                                            <!-- Caja de Detalles -->
                                            <div class="details-box">
                                                <table class="details-table" role="presentation">
                                                    <tr>
                                                        <td class="label">Compañía:</td>
                                                        <td class="value"><strong>%s</strong></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="label">Servicio:</td>
                                                        <td class="value">%s</td>
                                                    </tr>
                                                    <tr>
                                                        <td class="label">Fecha:</td>
                                                        <td class="value">%s</td>
                                                    </tr>
                                                    <tr>
                                                        <td class="label">Hora:</td>
                                                        <td class="value">%s</td>
                                                    </tr>
                                                    <tr>
                                                        <td class="label">Duración:</td>
                                                        <td class="value">%s minutos</td>
                                                    </tr>
                                                    <tr>
                                                        <td class="label">Lugar:</td>
                                                        <td class="value">%s</td>
                                                    </tr>
                                                    <tr>
                                                        <td class="label">Precio:</td>
                                                        <td class="value">%s</td>
                                                    </tr>
                                                </table>
                                            </div>
                                        </td>
                                    </tr>
                                    <!-- Pie de página -->
                                    <tr>
                                        <td class="footer">
                                            Este es un correo automático, por favor no respondas a este mensaje.<br>
                                            © 2026 Slotify. Todos los derechos reservados.
                                        </td>
                                    </tr>
                                </table>
                            </center>
                                        
                        </body>
                        </html>
                """, companyName, service, date, time, minutesDuration, physicalAddress, price);
    }

    private String generateCompanyTemplate(String username, String serviceName, String date, String time, String minutesDuration) {
        return String.format("""
                        <!DOCTYPE html>
                        <html lang="es">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Nueva Reserva</title>
                            <style>
                                body {
                                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                                    background-color: #f4f6f8;
                                    margin: 0;
                                    padding: 0;
                                    -webkit-font-smoothing: antialiased;
                                    width: 100%% !important;
                                }
                                .wrapper {
                                    width: 100%%;
                                    table-layout: fixed;
                                    background-color: #f4f6f8;
                                    padding-bottom: 40px;
                                    padding-top: 40px;
                                }
                                .main-table {
                                    background-color: #ffffff;
                                    margin: 0 auto;
                                    width: 100%%;
                                    max-width: 600px;
                                    border-spacing: 0;
                                    font-family: sans-serif;
                                    color: #333333;
                                    border-radius: 8px;
                                    overflow: hidden;
                                    box-shadow: 0 4px 10px rgba(0,0,0,0.05);
                                }
                                .header-strip {
                                    background-color: #198754;
                                    height: 6px;
                                }
                                .content {
                                    padding: 40px 30px;
                                }
                                .title {
                                    font-size: 24px;
                                    font-weight: bold;
                                    color: #1d2124;
                                    margin-bottom: 20px;
                                }
                                .message {
                                    font-size: 16px;
                                    line-height: 1.6;
                                    color: #555555;
                                    margin-bottom: 30px;
                                }
                                .details-box {
                                    background-color: #f8f9fa;
                                    border-left: 4px solid #198754;
                                    padding: 20px;
                                    margin-bottom: 30px;
                                    border-radius: 0 4px 4px 0;
                                }
                                .details-table {
                                    width: 100%%;
                                    border-spacing: 0;
                                }
                                .details-table td {
                                    padding: 6px 0;
                                    font-size: 15px;
                                }
                                .label {
                                    font-weight: bold;
                                    color: #6c757d;
                                    width: 35%%;
                                }
                                .value {
                                    color: #212529;
                                }
                                .footer {
                                    background-color: #f8f9fa;
                                    padding: 20px;
                                    text-align: center;
                                    font-size: 12px;
                                    color: #6c757d;
                                    border-top: 1px solid #eeeeee;
                                }
                            </style>
                        </head>
                        <body>
                                        
                            <center class="wrapper">
                                <table class="main-table" role="presentation">
                                    <!-- Línea de color superior -->
                                    <tr>
                                        <td class="header-strip"></td>
                                    </tr>
                                    <!-- Contenido Principal -->
                                    <tr>
                                        <td class="content">
                                            <h1 class="title">Nueva reserva</h1>
                                            <p class="message">
                                                Hola, un cliente ha hecho una reserva, te adjuntamos los detalles:
                                            </p>
                                            <!-- Caja de Detalles -->
                                            <div class="details-box">
                                                <table class="details-table" role="presentation">
                                                    <tr>
                                                        <td class="label">Usuario:</td>
                                                        <td class="value"><strong>%s</strong></td>
                                                    </tr>
                                                    <tr>
                                                        <td class="label">Servicio:</td>
                                                        <td class="value">%s</td>
                                                    </tr>
                                                    <tr>
                                                        <td class="label">Fecha:</td>
                                                        <td class="value">%s</td>
                                                    </tr>
                                                    <tr>
                                                        <td class="label">Hora:</td>
                                                        <td class="value">%s</td>
                                                    </tr>
                                                    <tr>
                                                        <td class="label">Duración:</td>
                                                        <td class="value">%s minutos</td>
                                                    </tr>
                                                </table>
                                            </div>
                                        </td>
                                    </tr>
                                    <!-- Pie de página -->
                                    <tr>
                                        <td class="footer">
                                            Este es un correo automático, por favor no respondas a este mensaje.<br>
                                            © 2026 Slotify. Todos los derechos reservados.
                                        </td>
                                    </tr>
                                </table>
                            </center>
                                        
                        </body>
                        </html>
                """, username, serviceName, date, time, minutesDuration);
    }
}
