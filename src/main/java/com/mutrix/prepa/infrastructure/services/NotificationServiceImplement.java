package com.mutrix.prepa.infrastructure.services;

import com.mutrix.prepa.domaines.models.NotificationModel;
import com.mutrix.prepa.domaines.notifications.NotificationsChannel;
import com.mutrix.prepa.domaines.services.NotificationService;
import com.mutrix.prepa.domaines.valueobjects.NotificationType;
import com.mutrix.prepa.infrastructure.notifications.factories.NotificationFactory;
import com.mutrix.prepa.infrastructure.notifications.providers.emails.OtpEmailTemplate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class NotificationServiceImplement implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImplement.class);

    private final NotificationFactory notificationFactory;

    @Override
    public void sendOtpEmail(String email, String otp) {
        sendOtpEmail(email, otp, null);
    }

    /**
     * Variante enrichie avec le nom complet du destinataire pour personnaliser le template.
     */
    public void sendOtpEmail(String email, String otp, String fullName) {
        try {
            final NotificationsChannel channel = notificationFactory.create(NotificationType.EMAIL);

            // Génère le HTML complet via le template OTP
            String htmlBody = OtpEmailTemplate.build(otp, fullName);

            Map<String, Object> meta = Map.of("subject", "Votre code de vérification – Prepa Concours");

            channel.sendNotification(
                    NotificationModel.builder()
                            .title("Code de vérification")
                            .receiver(List.of(email))
                            .body(htmlBody)
                            .metaData(meta)
                            .build()
            );
        } catch (Exception e) {
            log.error("Échec d'envoi de l'OTP par e-mail à {} : {}", email, e.getMessage(), e);
            throw new RuntimeException("Échec d'envoi de l'OTP par e-mail : " + e.getMessage(), e);
        }
    }

    @Override
    public void sendOtpSms(String phone, String otp) {
        try {
            final NotificationsChannel channel = notificationFactory.create(NotificationType.SMS);
//            channel.sendNotification(phone, "Your Prepa Concours OTP Code", "Your  Prepa Concours OTP code is: " + otp,
//                    null);
            channel.sendNotification(NotificationModel.builder()
                    .title("Your Prepa Concours OTP Code")
                    .receiver(List.of(phone))
                    .body("Your  Prepa Concours OTP code is: " + otp)
                    .metaData(null)
                    .build());
        } catch (Exception e) {
            System.err.println("Failed to send OTP SMS: " + e.getMessage());
            log.error("Failed to send OTP SMS to {}: {}", phone, e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP SMS: " + e.getMessage(), e);
        }

    }

    @Override
    public void sendWhatsAppOtp(String phone, String otp) {
        try {
            final NotificationsChannel channel = notificationFactory.create(NotificationType.WHATSAPP);
//            channel.sendNotification(phone, "Your Prepa Concours OTP Code", message, null);
            channel.sendNotification(NotificationModel.builder()
                    .title("Your Prepa Concours OTP Code")
                    .receiver(List.of(phone))
                    .body("Your  Prepa Concours OTP code is: " + otp)
                    .metaData(null)
                    .build());
        } catch (Exception e) {
            System.err.println("Failed to send OTP WhatsApp message: " + e.getMessage());
            log.error("Failed to send OTP WhatsApp message to {}: {}", phone, e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP WhatsApp message: " + e.getMessage(), e);
        }
    }
}
