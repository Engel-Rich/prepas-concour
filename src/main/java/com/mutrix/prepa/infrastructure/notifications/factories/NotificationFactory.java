package com.mutrix.prepa.infrastructure.notifications.factories;

import com.mutrix.prepa.domaines.notifications.NotificationsChannel;
import com.mutrix.prepa.domaines.valueobjects.NotificationType;
import com.mutrix.prepa.infrastructure.notifications.channels.EmailNotificationChannel;
import com.mutrix.prepa.infrastructure.notifications.channels.PushNotificationChannel;
import com.mutrix.prepa.infrastructure.notifications.channels.SmsNotificationchannel;
import com.mutrix.prepa.infrastructure.notifications.channels.WhatsAppNotificationChannel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class NotificationFactory {
    private EmailNotificationChannel emailNotificationService;
    private SmsNotificationchannel smsNotificationService;
    private WhatsAppNotificationChannel whatsAppNotificationService;
    private PushNotificationChannel pushNotificationServices;

    public NotificationsChannel create(NotificationType type) {
        return switch (type) {
            case EMAIL -> emailNotificationService;
            case SMS -> smsNotificationService;
            case PUSH -> pushNotificationServices;
            case WHATSAPP -> whatsAppNotificationService;
            default -> throw new IllegalArgumentException("Invalid notification type: " + type);
        };
    }
}
