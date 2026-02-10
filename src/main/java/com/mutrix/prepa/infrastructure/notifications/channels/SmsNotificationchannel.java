package com.mutrix.prepa.infrastructure.notifications.channels;

import com.mutrix.prepa.domaines.notifications.NotificationsChannel;
import com.mutrix.prepa.domaines.notifications.providers.SmsNotificationProvider;

import java.util.Map;

public class SmsNotificationchannel implements NotificationsChannel {
    SmsNotificationProvider smsNotificationProvider;
    @Override
    public void sendNotification(String chanelValueElement, String title, String body, Map<String, Object> data) {
        // Generate template with title and body
        String content = "Title: " + title + "\n" + "Body: " + body;
        smsNotificationProvider.sendSmsNotification(chanelValueElement, content);
    }
}
