package com.mutrix.prepa.infrastructure.notifications.providers.whatsapp;

import com.mutrix.prepa.domaines.notifications.providers.WhatsAppNotificationProvider;
import org.springframework.stereotype.Component;

@Component
public class D7NetworkNotificationProvider implements WhatsAppNotificationProvider {
    @Override
    public void sendWhatsAppNotification(String phoneNumber, String message) {
        // Implement D7 Network API call here
        System.out.println("Sending WhatsApp message via D7 Network to: " + phoneNumber);
        System.out.println("Message: " + message);
    }
}
