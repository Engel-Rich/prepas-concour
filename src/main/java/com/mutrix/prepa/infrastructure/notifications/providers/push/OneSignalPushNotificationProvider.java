package com.mutrix.prepa.infrastructure.notifications.providers.push;

import com.mutrix.prepa.domaines.notifications.providers.PushNotificationProvider;

public class OneSignalPushNotificationProvider implements PushNotificationProvider {

    @Override
    public void sendPushNotification(java.util.List<String> deviceToken, String title, String message, java.util.Map<String, Object> data) {
        // Implement OneSignal API call here
        System.out.println("Sending push notification via OneSignal to device tokens: " + deviceToken);
    }
}
