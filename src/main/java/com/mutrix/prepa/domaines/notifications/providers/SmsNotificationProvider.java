package com.mutrix.prepa.domaines.notifications.providers;

public interface SmsNotificationProvider {
    public void sendSmsNotification(String phoneNumber, String message);
}
