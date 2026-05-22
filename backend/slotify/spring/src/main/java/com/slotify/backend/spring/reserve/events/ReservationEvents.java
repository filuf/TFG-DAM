package com.slotify.backend.spring.reserve.events;

import com.slotify.backend.spring.company.models.CompanyEntity;
import com.slotify.backend.spring.mailer.MailService;
import com.slotify.backend.spring.mailer.SendMailRequest;
import com.slotify.backend.spring.notification.models.NotificationEntity;
import com.slotify.backend.spring.notification.models.NotificationSender;
import com.slotify.backend.spring.notification.services.NotificationService;
import com.slotify.backend.spring.reserve.dtos.ReservationCanceledEvent;
import com.slotify.backend.spring.reserve.models.ReserveEntity;
import com.slotify.backend.spring.service.models.ServiceEntity;
import com.slotify.backend.spring.user.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReservationEvents {

    private final MailService mailService;
    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendMail(ReservationCanceledEvent event) throws Exception {

        SendMailRequest sendMailRequest = switch (event.getAccountType()) {
            case COMPANY -> this.generateUserMailRequest(event);
            case USER -> this.generateCompanyMailRequest(event);
        };

        this.mailService.sendValidMail(sendMailRequest);
    }

    @EventListener
    public void saveCancelNotification(ReservationCanceledEvent event) {
        ReserveEntity reserve = event.getReserveEntity();
        ServiceEntity service = reserve.getService();

        String day = reserve.getServiceTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String hour = reserve.getServiceTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        NotificationEntity.NotificationEntityBuilder builder = NotificationEntity.builder()
                .company(service.getCompany())
                .user(reserve.getUser())
                .createdAt(LocalDateTime.now());

        String textContent = switch (event.getAccountType()) {
            case COMPANY -> {
                builder.notificationSender(NotificationSender.COMPANY);
                yield String.format("La reserva en %s para %s para el día %s a la hora %s ha sido cancelada",
                        service.getCompany().getCompanyName(), service.getServiceName(), day, hour);
            }
            case USER -> {
                builder.notificationSender(NotificationSender.USER);
                yield String.format("La reserva de %s para el día %s a la hora %s ha sido cancelada",
                        reserve.getUser().getUsername(), day, hour);
            }
        };

        this.notificationService.saveNotification(builder.textContent(textContent).build());
    }

    private SendMailRequest generateCompanyMailRequest(ReservationCanceledEvent event) {
        ReserveEntity reserve = event.getReserveEntity();
        CompanyEntity company = reserve.getService().getCompany();
        UserEntity user = reserve.getUser();

        LocalDateTime serviceTime = reserve.getServiceTime();


        return SendMailRequest.builder()
                .sendToEmail(company.getEmailAddress())
                .subject("Reserva cancelada")
                .text(this.generateCompanyTemplate(
                        user.getUsername(),
                        reserve.getService().getServiceName(),
                        serviceTime.toLocalDate().toString(),
                        serviceTime.toLocalTime().toString()))
                .build();
    }

    private SendMailRequest generateUserMailRequest(ReservationCanceledEvent event) {
        ReserveEntity reserve = event.getReserveEntity();
        CompanyEntity company = reserve.getService().getCompany();
        UserEntity user = reserve.getUser();

        LocalDateTime serviceTime = reserve.getServiceTime();

        return SendMailRequest.builder()
                .sendToEmail(user.getEmailAddress())
                .subject("Reserva cancelada")
                .text(this.generateUserTemplate(
                        company.getCompanyName(),
                        reserve.getService().getServiceName(),
                        serviceTime.toLocalDate().toString(),
                        serviceTime.toLocalTime().toString()))
                .build();
    }

    private String generateUserTemplate(String companyName, String service, String date, String time) {
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
                            background-color: #dc3545; /* Rojo para indicar cancelación */
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
                            border-left: 4px solid #dc3545;
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
                                    <h1 class="title">Reserva Cancelada</h1>
                                    <p class="message">
                                        Hola, te informamos que se ha cancelado una reserva activa. A continuación te mostramos los detalles del movimiento:
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
        """, companyName, service, date, time);
    }

    private String generateCompanyTemplate(String username, String service, String date, String time) {
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
                            background-color: #dc3545; /* Rojo para indicar cancelación */
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
                            border-left: 4px solid #dc3545;
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
                                    <h1 class="title">Reserva Cancelada</h1>
                                    <p class="message">
                                        Hola, te informamos que un usuario ha cancelado una reserva activa. A continuación te mostramos los detalles del movimiento:
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
                                        </table>
                                    </div>
                                    <p class="message" style="font-size: 14px; color: #888888; margin-bottom: 0;">
                                        El horario de esta cita vuelve a estar disponible para otros clientes de forma automática.
                                    </p>
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
        """, username, service, date, time);
    }

}
