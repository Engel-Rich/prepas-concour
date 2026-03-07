package com.mutrix.prepa.domaines.notifications.providers;

import java.util.List;

public interface EmailNotificationProvider {
    public void sendEmailNotification(String emailAddress, String subject, String message,
            List<String> ccEmailAddresses);
}
