package com.mutrix.prepa.infrastructure.notifications.providers.emails;

import com.mutrix.prepa.domaines.notifications.providers.EmailNotificationProvider;

import java.util.List;

public class ResendEmailProvider implements EmailNotificationProvider {

    @Override
    public void sendEmailNotification(String emailAddress, String subject, String message, List<String> ccEmailAddresses) {
        // Implement Resend API call here
        System.out.println("Sending email via Resend to: " + emailAddress);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        if (ccEmailAddresses != null && !ccEmailAddresses.isEmpty()) {
            System.out.println("CC: " + ccEmailAddresses);
        }
    }
}
