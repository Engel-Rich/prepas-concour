package com.mutrix.prepa.infrastructure.notifications.channels;

import java.util.List;
import java.util.Map;

import com.mutrix.prepa.domaines.models.NotificationModel;
import org.springframework.stereotype.Component;

import com.mutrix.prepa.domaines.notifications.NotificationsChannel;
import com.mutrix.prepa.domaines.notifications.providers.PushNotificationProvider;
import com.mutrix.prepa.infrastructure.notifications.factories.PushNotificationFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PushNotificationChannel implements NotificationsChannel {

    private final String pushProviderType = "FIREBASE";;
    private final PushNotificationFactory pushNotificationFactory;

    @Override
    public void sendNotification(NotificationModel notification) {
        // Generate template with title and body
        List<String> deviceTokens = notification.getReceiver();
        PushNotificationProvider provider = pushNotificationFactory.create(pushProviderType);
        provider.sendPushNotification(deviceTokens, notification.getTitle(), notification.getBody(), notification.getMetaData());
    }
}
