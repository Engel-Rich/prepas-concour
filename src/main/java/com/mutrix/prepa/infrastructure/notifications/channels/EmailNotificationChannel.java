package com.mutrix.prepa.infrastructure.notifications.channels;

import com.mutrix.prepa.domaines.notifications.NotificationsChannel;
import com.mutrix.prepa.domaines.notifications.providers.EmailNotificationProvider;

import java.util.List;
import java.util.Map;

public class EmailNotificationChannel implements NotificationsChannel {
    EmailNotificationProvider emailNotificationProvider;

    @Override
    public void sendNotification(String chanelValueElement, String title, String body, Map<String,Object> data) {
        String subject = "";
        List<String> ccEmailAddresses = null;
        if(data!=null){
            subject =  data.containsKey("subject") ? (String) data.get("subject") : "";

            Object ccObj = data.get("ccEmailAddresses");

            if (ccObj instanceof List<?>) {
                // Un petit stream pour garantir que ce sont bien des Strings
                ccEmailAddresses = ((List<?>) ccObj).stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .toList();
            }
        }
        // Generate template with title and body
        String content = "Title: " + title + "\n" + "Body: " + body;
        emailNotificationProvider.sendEmailNotification(chanelValueElement, subject, content, ccEmailAddresses);
    }
}


