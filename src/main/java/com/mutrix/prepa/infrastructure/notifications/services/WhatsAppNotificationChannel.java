package com.mutrix.prepa.infrastructure.notifications.services;

import com.mutrix.prepa.domaines.notifications.NotificationsChannel;
import com.mutrix.prepa.domaines.notifications.providers.WhatsAppNotificationProvider;

import java.util.Map;

public class WhatsAppNotificationChannel implements NotificationsChannel {
    WhatsAppNotificationProvider whatsAppNotificationProvider;

    @Override
    public void sendNotification(String chanelValueElement, String title, String body, Map<String, Object> data) {
        // Generate template with title and body
        String content = "Title: " + title + "\n" + "Body: " + body;
        whatsAppNotificationProvider.sendWhatsAppNotification(chanelValueElement, content);
    }
}
