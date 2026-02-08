package com.mutrix.prepa.infrastructure.notifications.services;

import com.mutrix.prepa.domaines.notifications.NotificationsChannel;
import com.mutrix.prepa.domaines.notifications.providers.PushNotificationProvider;

import java.util.List;
import java.util.Map;

public class PushNotificationChannel implements NotificationsChannel {

    PushNotificationProvider provider;

    @Override
    public void sendNotification(String chanelValueElement, String title, String body, Map<String, Object> data) {

        // Generate template with title and body
        String content = "Title: " + title + "\n" + "Body: " + body;
        List<String> deviceTokens = List.of(chanelValueElement);
        provider.sendPushNotification(deviceTokens,  title, body, data);
    }
}
