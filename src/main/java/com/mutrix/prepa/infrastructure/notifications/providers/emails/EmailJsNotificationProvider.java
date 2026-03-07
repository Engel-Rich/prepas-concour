package com.mutrix.prepa.infrastructure.notifications.providers.emails;

import com.mutrix.prepa.domaines.notifications.providers.EmailNotificationProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailJsNotificationProvider implements EmailNotificationProvider {
    @Override
    public void sendEmailNotification(String emailAddress, String subject, String message, List<String> ccEmailAddresses) {
        // Implement EmailJS API call here
        System.out.println("Sending email to: " + emailAddress);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        if (ccEmailAddresses != null && !ccEmailAddresses.isEmpty()) {
            System.out.println("CC: " + ccEmailAddresses);
        }
    }
}
