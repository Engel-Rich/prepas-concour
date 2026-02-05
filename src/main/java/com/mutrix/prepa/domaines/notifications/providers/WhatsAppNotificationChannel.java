package com.mutrix.prepa.domaines.notifications.providers;

public interface WhatsAppNotificationChannel {
    public  void  sendWhatsAppNotification(String phoneNumber, String message);

}
