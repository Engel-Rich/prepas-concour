package com.mutrix.prepa.infrastructure.notifications.channels;

import java.util.Map;

import com.mutrix.prepa.domaines.models.NotificationModel;
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
    public void sendNotification(NotificationModel notification) {

        // Generate template with title and body
        String content = "Title: " + notification.getTitle() + "\n" + "Body: " + notification.getBody();
        SmsNotificationProvider smsNotificationProvider = smsNotificationFactory.create(smsProviderType);
        notification.getReceiver().forEach((chanelValueElement)->{
            smsNotificationProvider.sendSmsNotification(chanelValueElement, content);
        });
    }
}
