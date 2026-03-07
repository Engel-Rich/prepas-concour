package com.mutrix.prepa.infrastructure.services;

import com.mutrix.prepa.domaines.notifications.NotificationsChannel;
import com.mutrix.prepa.domaines.services.NotificationService;
import com.mutrix.prepa.domaines.valueobjects.NotificationType;
import com.mutrix.prepa.infrastructure.notifications.factories.NotificationFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class NotificationServiceImplement implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImplement.class);

    private final NotificationFactory notificationFactory;

    @Override
    public void sendOtpEmail(String email, String otp) {
        try {
            final NotificationsChannel channel = notificationFactory.create(NotificationType.EMAIL);
            Map<String, Object> data = Map.of("subject", "Prepa Concours OTP Code");
            channel.sendNotification(email, "Your Prepa Concours OTP Code", "Your  Prepa Concours OTP code is: " + otp,
                    data);
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
            log.error("Failed to send OTP email to {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendOtpSms(String phone, String otp) {
        try {
            final NotificationsChannel channel = notificationFactory.create(NotificationType.SMS);
            channel.sendNotification(phone, "Your Prepa Concours OTP Code", "Your  Prepa Concours OTP code is: " + otp,
                    null);
        } catch (Exception e) {
            System.err.println("Failed to send OTP SMS: " + e.getMessage());
            log.error("Failed to send OTP SMS to {}: {}", phone, e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP SMS: " + e.getMessage(), e);
        }

    }

    @Override
    public void sendWhatsAppOtp(String phone, String message) {
        try {
            final NotificationsChannel channel = notificationFactory.create(NotificationType.WHATSAPP);
            channel.sendNotification(phone, "Your Prepa Concours OTP Code", message, null);
        } catch (Exception e) {
            System.err.println("Failed to send OTP WhatsApp message: " + e.getMessage());
            log.error("Failed to send OTP WhatsApp message to {}: {}", phone, e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP WhatsApp message: " + e.getMessage(), e);
        }
    }
}
