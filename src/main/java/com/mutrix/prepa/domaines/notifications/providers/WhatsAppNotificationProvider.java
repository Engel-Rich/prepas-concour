package com.mutrix.prepa.domaines.notifications.providers;

public interface WhatsAppNotificationProvider {
    public  void  sendWhatsAppNotification(String phoneNumber, String message);

}
