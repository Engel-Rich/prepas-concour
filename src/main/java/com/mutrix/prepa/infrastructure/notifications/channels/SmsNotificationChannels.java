package com.mutrix.prepa.infrastructure.notifications.channels;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.mutrix.prepa.domaines.notifications.NotificationsChannel;
import com.mutrix.prepa.domaines.notifications.providers.SmsNotificationProvider;
import com.mutrix.prepa.infrastructure.notifications.factories.SmsNotificationFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SmsNotificationChannels implements NotificationsChannel {

    private final String smsProviderType = "MONPROF";
    private final SmsNotificationFactory smsNotificationFactory;

    @Override
    public void sendNotification(String chanelValueElement, String title, String body, Map<String, Object> data) {

        // Generate template with title and body
        String content = "Title: " + title + "\n" + "Body: " + body;
        SmsNotificationProvider smsNotificationProvider = smsNotificationFactory.create(smsProviderType);
        smsNotificationProvider.sendSmsNotification(chanelValueElement, content);
    }
}
