package com.mutrix.prepa.domaines.notifications;

import java.util.Map;

public interface NotificationsChannel {
        public void sendNotification(String chanelValueElement, String title, String body, Map<String,Object> data);
}
