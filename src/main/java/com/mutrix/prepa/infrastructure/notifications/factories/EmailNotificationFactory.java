package com.mutrix.prepa.infrastructure.notifications.factories;

import com.mutrix.prepa.domaines.notifications.providers.EmailNotificationProvider;
import com.mutrix.prepa.infrastructure.notifications.providers.emails.EmailJsNotificationProvider;
import com.mutrix.prepa.infrastructure.notifications.providers.emails.ResendEmailProvider;
//import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class EmailNotificationFactory {
    private final EmailJsNotificationProvider emailJsNotificationProvider;
    private final ResendEmailProvider resendEmailProvider;

    public EmailNotificationProvider create(String type) {
        return switch (type) {
            case "EMAIL_JS" -> emailJsNotificationProvider;
            case "RESEND" -> resendEmailProvider;
            default -> throw new IllegalArgumentException("Invalid email notification provider type: " + type);
        };
    }
}
