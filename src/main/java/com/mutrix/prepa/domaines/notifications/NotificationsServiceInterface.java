package com.mutrix.prepa.domaines.notifications;

public interface NotificationsServiceInterface {
        public void sendNotification(String chanelValueElement, String title, String body, Object data);
}
