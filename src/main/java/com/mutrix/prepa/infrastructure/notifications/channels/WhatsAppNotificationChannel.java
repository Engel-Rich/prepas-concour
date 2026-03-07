package com.mutrix.prepa.infrastructure.notifications.channels;

import java.util.Map;

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
    public void sendNotification(String chanelValueElement, String title, String body, Map<String, Object> data) {
        // Generate template with title and body
        String content = "Title: " + title + "\n" + "Body: " + body;
        WhatsAppNotificationProvider whatsAppNotificationProvider = whatsAppNotificationFactory
                .create(whatsAppProviderType);
        whatsAppNotificationProvider.sendWhatsAppNotification(chanelValueElement, content);
    }
}
