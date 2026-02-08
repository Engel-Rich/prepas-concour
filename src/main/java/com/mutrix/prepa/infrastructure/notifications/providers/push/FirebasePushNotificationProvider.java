package com.mutrix.prepa.infrastructure.notifications.providers.push;

import com.mutrix.prepa.domaines.notifications.providers.PushNotificationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class FirebasePushNotificationProvider implements PushNotificationProvider {


    private static final Logger log = LoggerFactory.getLogger(FirebasePushNotificationProvider.class);

    @Override
    public void sendPushNotification(List<String> deviceToken, String title, String message, Map<String, Object> data) {
        log.info("Sending push notification to device tokens: {}", deviceToken);
    }
}
