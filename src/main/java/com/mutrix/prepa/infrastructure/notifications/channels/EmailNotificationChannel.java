package com.mutrix.prepa.infrastructure.notifications.channels;

import java.util.List;
import java.util.Map;

import com.mutrix.prepa.domaines.models.NotificationModel;
import org.springframework.stereotype.Component;

import com.mutrix.prepa.domaines.notifications.NotificationsChannel;
import com.mutrix.prepa.domaines.notifications.providers.EmailNotificationProvider;
import com.mutrix.prepa.infrastructure.notifications.factories.EmailNotificationFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailNotificationChannel implements NotificationsChannel {
    private final EmailNotificationFactory emailNotificationFactory;
    private final String emailProviderType = "RESEND";;

    @Override
    public void sendNotification(NotificationModel notificationModel) {
        String subject = "";
        List<String> ccEmailAddresses = null;
        if (notificationModel.getMetaData() != null) {
            Map<String, Object> data = notificationModel.getMetaData();
            subject = data.containsKey("subject") ? (String) data.get("subject") : "";

            Object ccObj = data.get("ccEmailAddresses");

            if (ccObj instanceof List<?>) {
                // Un petit stream pour garantir que ce sont bien des Strings
                ccEmailAddresses = ((List<?>) ccObj).stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .toList();
            }
        }
        // Le body contient déjà le HTML complet construit par le service appelant
        String content = notificationModel.getBody() != null ? notificationModel.getBody() : "";
        EmailNotificationProvider emailNotificationProvider = emailNotificationFactory.create(emailProviderType);
        emailNotificationProvider.sendEmailNotification(notificationModel.getReceiver(), subject, content, ccEmailAddresses);
    }
}
