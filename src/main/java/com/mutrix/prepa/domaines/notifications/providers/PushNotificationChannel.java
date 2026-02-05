package com.mutrix.prepa.domaines.notifications.providers;

import java.util.List;
import java.util.Map;

public interface PushNotificationChannel {
   public void sendPushNotification(List<String> deviceToken, String title, String message, Map<String, Object> data);
}
