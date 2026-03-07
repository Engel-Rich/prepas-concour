package com.mutrix.prepa.infrastructure.notifications.factories;

import com.mutrix.prepa.domaines.notifications.NotificationsChannel;
import com.mutrix.prepa.domaines.valueobjects.NotificationType;
import com.mutrix.prepa.infrastructure.notifications.channels.EmailNotificationChannel;
import com.mutrix.prepa.infrastructure.notifications.channels.PushNotificationChannel;
import com.mutrix.prepa.infrastructure.notifications.channels.SmsNotificationChannels;
import com.mutrix.prepa.infrastructure.notifications.channels.WhatsAppNotificationChannel;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationFactory {
    private final EmailNotificationChannel emailNotificationService;
    private final WhatsAppNotificationChannel whatsAppNotificationService;
    private final PushNotificationChannel pushNotificationServices;
    private final SmsNotificationChannels smsNotificationChannel;

    public NotificationsChannel create(NotificationType type) {
        return switch (type) {
            case EMAIL -> emailNotificationService;
            case SMS -> smsNotificationChannel;
            case PUSH -> pushNotificationServices;
            case WHATSAPP -> whatsAppNotificationService;
            default -> throw new IllegalArgumentException("Invalid notification type: " + type);
        };
    }
}
