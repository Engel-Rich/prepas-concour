package com.mutrix.prepa.domaines.notifications.providers;

import java.util.List;

public interface EmailNotificationProvider {
    public void sendEmailNotification(List<String> emailAddress, String subject, String message,
            List<String> ccEmailAddresses);
}
