package com.mutrix.prepa.domaines.notifications;

import com.mutrix.prepa.domaines.models.NotificationModel;

import java.util.Map;

public interface NotificationsChannel {
        public void sendNotification(NotificationModel notificationModel);
}
