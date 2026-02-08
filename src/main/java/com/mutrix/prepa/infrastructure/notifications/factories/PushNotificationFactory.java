package com.mutrix.prepa.infrastructure.notifications.factories;

import com.mutrix.prepa.domaines.notifications.providers.PushNotificationProvider;
import com.mutrix.prepa.infrastructure.notifications.providers.push.FirebasePushNotificationProvider;
import com.mutrix.prepa.infrastructure.notifications.providers.push.OneSignalPushNotificationProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PushNotificationFactory {
    private  final FirebasePushNotificationProvider firebasePushNotificationProvider;
    private  final OneSignalPushNotificationProvider oneSignalPushNotificationProvider;

    public PushNotificationProvider create(String type) {
        return switch (type) {
            case "FIREBASE" -> firebasePushNotificationProvider;
            case "ONE_SIGNAL" -> oneSignalPushNotificationProvider;
            default -> throw new IllegalArgumentException("Invalid push notification provider type: " + type);
        };
    }
}
