package com.mutrix.prepa.domaines.notifications.providers;

public interface SmsNotificationChannel {
    public void sendSmsNotification(String phoneNumber, String message);
}
