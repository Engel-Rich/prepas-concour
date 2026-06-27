package com.mutrix.prepa.infrastructure.notifications.channels;

import java.util.Map;

import com.mutrix.prepa.domaines.models.NotificationModel;
import org.springframework.stereotype.Component;

import com.mutrix.prepa.domaines.notifications.NotificationsChannel;
import com.mutrix.prepa.domaines.notifications.providers.WhatsAppNotificationProvider;
import com.mutrix.prepa.infrastructure.notifications.factories.WhatsAppNotificationFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WhatsAppNotificationChannel implements NotificationsChannel {

    private final String whatsAppProviderType = "D7_NETWORK";
    private final WhatsAppNotificationFactory whatsAppNotificationFactory;

    @Override
    public void sendNotification(NotificationModel notification) {
        // Generate template with title and body
        String content = "Title: " + notification.getTitle() + "\n" + "Body: " + notification.getBody();
        WhatsAppNotificationProvider whatsAppNotificationProvider = whatsAppNotificationFactory
                .create(whatsAppProviderType);
        notification.getReceiver().forEach((chanelValueElement)->{
            whatsAppNotificationProvider.sendWhatsAppNotification(chanelValueElement, content);
        });
    }
}
