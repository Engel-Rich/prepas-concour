package com.mutrix.prepa.infrastructure.notifications.providers.sms;

import com.mutrix.prepa.domaines.notifications.providers.SmsNotificationProvider;
import org.springframework.stereotype.Component;

@Component
public class TwillioSMSNotificationProvider implements SmsNotificationProvider {
    @Override
    public void sendSmsNotification(String phoneNumber, String message) {
        // Implement Twillio API call here
        System.out.println("Sending SMS message via Twillio to: " + phoneNumber);
        System.out.println("Message: " + message);
    }
}
